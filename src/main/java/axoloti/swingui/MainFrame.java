/**
 * Copyright (C) 2013 - 2016 Johannes Taelman
 *
 * This file is part of Axoloti.
 *
 * Axoloti is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Axoloti is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Axoloti. If not, see <http://www.gnu.org/licenses/>.
 */
package axoloti.swingui;

import axoloti.Axoloti;
import static axoloti.Axoloti.HOME_DIR;
import static axoloti.Axoloti.RELEASE_DIR;
import axoloti.Version;
import axoloti.abstractui.DocumentWindowList;
import axoloti.connection.CConnection;
import axoloti.connection.IConnection;
import axoloti.connection.IDevice;
import axoloti.connection.ILivePatch;
import axoloti.connection.IPatchCB;
import axoloti.connection.LivePatch;
import axoloti.connection.USBDeviceLister;
import axoloti.job.GlobalJobProcessor;
import axoloti.job.GlobalProgress;
import axoloti.job.IJobContext;
import axoloti.job.IProgressReporter;
import axoloti.job.JobContext;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.objectlibrary.AxoObjects;
import axoloti.objectlibrary.AxolotiLibrary;
import axoloti.patch.PatchController;
import axoloti.patch.PatchModel;
import axoloti.preferences.Preferences;
import axoloti.preferences.Theme;
import axoloti.swingui.patch.PatchFrame;
import axoloti.swingui.patch.PatchViewSwing;
import axoloti.swingui.patchbank.PatchBank;
import axoloti.swingui.preferences.ThemeEditor;
import axoloti.swingui.target.TJFrame;
import axoloti.target.TargetModel;
import axoloti.target.TargetRTInfo;
import axoloti.utils.KeyUtils;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.swing.BoundedRangeModel;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

/**
 *
 * @author Johannes Taelman
 */
public class MainFrame extends TJFrame implements ActionListener {

    public static MainFrame mainframe;

    private final ThemeEditor themeEditor;

    private boolean bGrabFocusOnSevereErrors = true;

    private boolean doAutoScroll = true;

    /**
     * Creates new form MainFrame
     *
     * @param args command line arguments
     */
    public MainFrame(String args[], TargetModel targetModel) {
        super(targetModel);
        initComponents();

        checkSpaceInPath();

        jLabelVolt50.setSize(jLabelVolt50.getPreferredSize());
        fileMenu.initComponents();

        mainframe = this;

        GlobalProgress.setInstance(getProgressReporter());

        final Style styleParent = jTextPaneLog.addStyle(null, null);
        StyleConstants.setFontFamily(styleParent, Font.MONOSPACED);

        final Style styleSevere = jTextPaneLog.addStyle("severe", styleParent);
        final Style styleInfo = jTextPaneLog.addStyle("info", styleParent);
        final Style styleWarning = jTextPaneLog.addStyle("warning", styleParent);
        jTextPaneLog.setBackground(Theme.getCurrentTheme().Console_Background);
        StyleConstants.setForeground(styleSevere, Theme.getCurrentTheme().Error_Text);
        StyleConstants.setForeground(styleInfo, Theme.getCurrentTheme().Normal_Text);
        StyleConstants.setForeground(styleWarning, Theme.getCurrentTheme().Warning_Text);

        DefaultCaret caret = (DefaultCaret) jTextPaneLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        jScrollPaneLog.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            private final BoundedRangeModel brm = jScrollPaneLog.getVerticalScrollBar().getModel();

            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                // Invoked when user select and move the cursor of scroll by mouse explicitly.
                if (!brm.getValueIsAdjusting()) {
                    if (doAutoScroll) {
                        brm.setValue(brm.getMaximum());
                    }
                } else {
                    // doAutoScroll will be set to true when user reaches at the bottom of document.
                    doAutoScroll = ((brm.getValue() + brm.getExtent()) == brm.getMaximum());
                }
            }
        });

        jScrollPaneLog.addMouseWheelListener(new MouseWheelListener() {
            private final BoundedRangeModel brm = jScrollPaneLog.getVerticalScrollBar().getModel();

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                // Invoked when user use mouse wheel to scroll
                if (e.getWheelRotation() < 0) {
                    // If user trying to scroll up, doAutoScroll should be false.
                    doAutoScroll = false;
                } else {
                    // doAutoScroll will be set to true when user reaches at the bottom of document.
                    doAutoScroll = ((brm.getValue() + brm.getExtent()) == brm.getMaximum());
                }
            }
        });

        Handler logHandler = new Handler() {
            @Override
            public void publish(LogRecord lr) {
                if (!SwingUtilities.isEventDispatchThread()) {
//                    System.out.println("logging outside GUI thread:"+lr.getMessage());
                    final LogRecord lrf = lr;
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            publish(lrf);
                        }
                    });
                } else {
                    try {
                        String txt;
                        String excTxt = "";
                        Throwable exc = lr.getThrown();
                        if (exc != null) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            PrintStream ps = new PrintStream(baos);
                            exc.printStackTrace(ps);
                            excTxt = exc.toString();
                            excTxt = excTxt + "\n" + baos.toString("utf-8");
                        }
                        if (lr.getMessage() == null) {
                            txt = excTxt;
                        } else {
                            txt = java.text.MessageFormat.format(lr.getMessage(), lr.getParameters());
                            if (excTxt.length() > 0) {
                                txt = txt + "," + excTxt;
                            }
                        }
                        if (lr.getLevel() == Level.SEVERE) {
                            doAutoScroll = true;
                            jTextPaneLog.getDocument().insertString(jTextPaneLog.getDocument().getLength(),
                                    txt + "\n", styleSevere);
                            if (bGrabFocusOnSevereErrors) {
                                MainFrame.this.toFront();
                            }
                        } else if (lr.getLevel() == Level.WARNING) {
                            jTextPaneLog.getDocument().insertString(jTextPaneLog.getDocument().getLength(),
                                    txt + "\n", styleWarning);
                        } else {
                            jTextPaneLog.getDocument().insertString(jTextPaneLog.getDocument().getLength(),
                                    txt + "\n", styleInfo);
                        }
                    } catch (BadLocationException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (UnsupportedEncodingException ex) {
                    }
                }
            }

            @Override
            public void flush() {
                jScrollPaneLog.removeAll();
            }

            @Override
            public void close() throws SecurityException {
            }
        };
        logHandler.setLevel(Level.INFO);

        Logger.getLogger("").addHandler(logHandler);
        Logger.getLogger("").setLevel(Level.INFO);

        themeEditor = new ThemeEditor();
        themeEditor.setTitle("Theme Editor");
        themeEditor.setVisible(false);

        if (!getTestDir(HOME_DIR, true)) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "Home directory is invalid:{0}, does it exist?, can it be written to?", System.getProperty(Axoloti.HOME_DIR));
        }

        if (!getTestDir(RELEASE_DIR, false)) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "Release directory is invalid:{0}, does it exist?", System.getProperty(Axoloti.RELEASE_DIR));
        }

        // do NOT do any serious initialisation in constructor
        // as a stalling error could prevent event loop running and our logging
        // console opening
        Runnable initr;
        initr = new Runnable() {
            @Override
            public void run() {
                try {
                    String tsuf = "";
                    if (Axoloti.isFailSafeMode()) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.WARNING, "Fail safe mode activated");
                        tsuf = "fail safe";
                    }
                    if (Axoloti.isDeveloper()) {
                        if (tsuf.length() > 0) {
                            tsuf += ",";
                        }
                        tsuf += "developer";
                    }
                    if (tsuf.length() > 0) {
                        MainFrame.this.setTitle(MainFrame.this.getTitle() + " (" + tsuf + ")");
                    }
                    Logger.getLogger(MainFrame.class.getName()).log(Level.INFO, "Axoloti version : {0}  build time : {1}", new Object[]{Version.AXOLOTI_VERSION, Version.AXOLOTI_BUILD_TIME});

                    // user library, ask user if they wish to upgrade, or do manuall
                    // this allows them the opportunity to manually backup their files!
                    AxolotiLibrary ulib = Preferences.getPreferences().getLibrary(AxolotiLibrary.USER_LIBRARY_ID);
                    if (ulib != null) {
                        String cb = ulib.getCurrentBranch();
                        if (!cb.equalsIgnoreCase(ulib.getBranch())) {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.INFO, "Current user library does not match correct version {0} -> {1}", new Object[]{cb, ulib.getBranch()});
                            int s = JOptionPane.showConfirmDialog(MainFrame.this,
                                    "User Library version mismatch, do you want to upgrade?\n"
                                    + "this will stash any changes, and then reapply to new version\n"
                                    + "if not, then you will need to manually backup changes, and then sync libraries",
                                    "User Library mismatch",
                                    JOptionPane.YES_NO_OPTION);
                            if (s == JOptionPane.YES_OPTION) {
                                ulib.upgrade();
                            }
                        }
                    }

                    // factory library force and upgrade
                    // Im stashing changes here, just in case, but in reality users should not be altering factory
                    ulib = Preferences.getPreferences().getLibrary(AxolotiLibrary.FACTORY_ID);
                    if (ulib != null) {
                        String cb = ulib.getCurrentBranch();
                        if (!cb.equalsIgnoreCase(ulib.getBranch())) {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.INFO, "Current factory library does not match correct version, upgrading {0} -> {1}", new Object[]{cb, ulib.getBranch()});
                            ulib.upgrade();
                        }
                    }

                    if (!Axoloti.isFailSafeMode()) {
                        for (AxolotiLibrary lib : Preferences.getPreferences().getLibraries()) {
                            if (lib.isAutoSync() && lib.getEnabled()) {
                                lib.sync();
                            }
                        }
                    }
                    for (AxolotiLibrary lib : Preferences.getPreferences().getLibraries()) {
                        lib.reportStatus();
                    }

                    IJobContext jobContext = new JobContext();
                    GlobalJobProcessor.getJobProcessor().exec((ctx) -> {
                        AxoObjects.loadAxoObjects(jobContext);
                    });

                    if (!Axoloti.isFailSafeMode()) {
                        boolean success = CConnection.getConnection().connect(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        EventQueue.invokeLater(initr);

        for (String arg : args) {
            if (!arg.startsWith("-")) {
                if (arg.endsWith(".axp") || arg.endsWith(".axs") || arg.endsWith(".axh")) {
                    final File f = new File(arg);
                    if (f.exists() && f.canRead()) {
                        // TODO: fix opening from command line
//                        Runnable r = new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    // wait for objects be loaded
//                                    if (AxoObjects.getAxoObjects().loaderThread.isAlive()) {
//                                        EventQueue.invokeLater(this);
//                                    } else {
//                                        PatchViewSwing.openPatch(f);
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        };
//                        EventQueue.invokeLater(r);
                    }
                } else if (arg.endsWith(".axo")) {
                    // NOP for AXO at the moment
                }
            }
        }

        init();
        USBDeviceLister.getInstance().registerHotplugCallback(hotplugCallback);
    }

    private boolean hasSpaceInPath(String p) {
        return p.indexOf(' ') != -1;
    }

    private void checkSpaceInPath() {
        if ((axoloti.utils.OSDetect.getOS() == axoloti.utils.OSDetect.OS.MAC)
                || (axoloti.utils.OSDetect.getOS() == axoloti.utils.OSDetect.OS.LINUX)) {
            if (hasSpaceInPath(Axoloti.getAPIDir())) {
                JOptionPane.showMessageDialog(null,
                        "Error: There is space character in the API path.\nPlease move/rename the path to one without space characters. Exiting...\n" + Axoloti.getAPIDir());
                Runtime.getRuntime().exit(-1);
            }
            if (hasSpaceInPath(Axoloti.getEnvDir())) {
                JOptionPane.showMessageDialog(null,
                        "Error: There is space character in the Env path.\nPlease move/rename the path to one without space characters. Exiting...\n" + Axoloti.getEnvDir());
                Runtime.getRuntime().exit(-1);
            }
            if (hasSpaceInPath(Axoloti.getHomeDir())) {
                JOptionPane.showMessageDialog(null,
                        "Error: There is space character in the Axoloti home path.\nPlease move/rename the path to one without space characters. Exiting...\n" + Axoloti.getHomeDir());
                Runtime.getRuntime().exit(-1);
            }
        }
        // on Windows it's be fine, paths can be "shortened" to 8.3 dos format.
    }

    private void init() {
        doLayout();
        model.getController().addView(this);
        jTablePatches.setModel(new AbstractTableModel() {
            private final String[] columnNames = {"PatchID", "name", "stop"};
            private final Class<?>[] columnTypes = {String.class, String.class, Boolean.class};

            @Override
            public int getRowCount() {
                List<Integer> patches = model.getPatchList();
                if (patches == null) {
                    return 0;
                }
                return patches.size();
            }

            @Override
            public int getColumnCount() {
                return columnNames.length;
            }

            @Override
            public String getColumnName(int column) {
                return columnNames[column];
            }

            @Override
            public Object getValueAt(int row, int column) {
                List<ILivePatch> patches = model.getPatchList();
                if (patches == null) {
                    return "null";
                }
                switch (column) {
                    case 0: //patchID
                        return String.format("0x%08x", ((LivePatch) patches.get(row)).getPatchRef());
                    case 1: //name
                        return patches.get(row).getName();
                    case 2:
                        return false;
                }
                return "?";
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                List<ILivePatch> patches = model.getPatchList();
                if (patches == null) {
                    return;
                }
                try {
                    patches.get(rowIndex).transmitStop();
                } catch (IOException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex == 2;
            }

            @Override
            public Class getColumnClass(int column) {
                return columnTypes[column];
            }
        });
    }

    private Runnable hotplugCallback = () -> {
        if (!CConnection.getConnection().isConnected()) {
            IDevice dev = USBDeviceLister.getInstance().getDefaultDevice();
            if (dev != null) {
                CConnection.getConnection().connect(dev);
            }
        }
    };

    static boolean getTestDir(String var, boolean write) {
        String ev = System.getProperty(var);
        File f = new File(ev);
        if (!f.exists()) {
            return false;
        }
        if (!f.isDirectory()) {
            return false;
        }
        if (write && !f.canWrite()) {
            return false;
        }

        return true;
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabelIcon = new javax.swing.JLabel();
        jButtonClear = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jCheckBoxConnect = new javax.swing.JCheckBox();
        jLabelCPUID = new javax.swing.JLabel();
        jLabelFirmwareID = new javax.swing.JLabel();
        jLabelVolt50 = new javax.swing.JLabel();
        jLabelVolt33 = new javax.swing.JLabel();
        jLabelSDCardPresent = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0), new java.awt.Dimension(32767, 0));
        jPanel5 = new javax.swing.JPanel();
        jPanel4 = new axoloti.swingui.target.TargetRTInfo(getDModel());
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTablePatches = new javax.swing.JTable();
        jScrollPaneLog = new javax.swing.JScrollPane();
        jTextPaneLog = new javax.swing.JTextPane();
        jProgressBar1 = new javax.swing.JProgressBar();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new axoloti.swingui.menus.FileMenu();
        jMenuEdit = new javax.swing.JMenu();
        jMenuItemCopy = new javax.swing.JMenuItem();
        jMenuBoard = new axoloti.swingui.target.TargetMenu(getDModel());
        windowMenu1 = new axoloti.swingui.menus.WindowMenu();
        helpMenu1 = new axoloti.swingui.menus.HelpMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Axoloti");
        setMinimumSize(new java.awt.Dimension(200, 200));
        setPreferredSize(new java.awt.Dimension(600, 400));
        setSize(new java.awt.Dimension(600, 400));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.PAGE_AXIS));

        jPanel2.setAlignmentX(0.0F);
        jPanel2.setMinimumSize(new java.awt.Dimension(246, 155));
        jPanel2.setPreferredSize(new java.awt.Dimension(272, 0));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.PAGE_AXIS));

        jLabelIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/axoloti_icon.png"))); // NOI18N
        jPanel3.add(jLabelIcon);

        jButtonClear.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        jButtonClear.setText("Clear");
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonClear);

        jPanel2.add(jPanel3);

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.PAGE_AXIS));

        jCheckBoxConnect.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        jCheckBoxConnect.setText("Connect");
        jCheckBoxConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxConnectActionPerformed(evt);
            }
        });
        jPanel1.add(jCheckBoxConnect);

        jLabelCPUID.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        jLabelCPUID.setText("CPUID");
        jPanel1.add(jLabelCPUID);

        jLabelFirmwareID.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        jLabelFirmwareID.setText("FirmwareID");
        jPanel1.add(jLabelFirmwareID);

        jLabelVolt50.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        jLabelVolt50.setText("5V  : -.--V");
        jPanel1.add(jLabelVolt50);

        jLabelVolt33.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        jLabelVolt33.setText("VDD : -.--V ");
        jPanel1.add(jLabelVolt33);

        jLabelSDCardPresent.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        jLabelSDCardPresent.setText("no SDCard");
        jPanel1.add(jLabelSDCardPresent);

        jPanel2.add(jPanel1);
        jPanel2.add(filler3);

        jPanel5.setAlignmentX(1.0F);
        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.PAGE_AXIS));

        getDModel().getController().addView((axoloti.swingui.target.TargetRTInfo)jPanel4);
        jPanel5.add(jPanel4);
        jPanel5.add(filler2);

        jPanel2.add(jPanel5);

        getContentPane().add(jPanel2);

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jScrollPane1.setAlignmentX(0.0F);
        jScrollPane1.setMinimumSize(new java.awt.Dimension(23, 50));

        jTablePatches.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jTablePatches.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTablePatches.setAlignmentX(0.0F);
        jTablePatches.setMinimumSize(new java.awt.Dimension(60, 94));
        jTablePatches.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTablePatchesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTablePatches);

        jSplitPane1.setLeftComponent(jScrollPane1);

        jScrollPaneLog.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPaneLog.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPaneLog.setAlignmentX(0.0F);
        jScrollPaneLog.setMinimumSize(new java.awt.Dimension(123, 53));
        jScrollPaneLog.setPreferredSize(new java.awt.Dimension(32767, 32767));

        jTextPaneLog.setEditable(false);
        jTextPaneLog.setAlignmentX(0.0F);
        jScrollPaneLog.setViewportView(jTextPaneLog);

        jSplitPane1.setRightComponent(jScrollPaneLog);

        getContentPane().add(jSplitPane1);

        jProgressBar1.setAlignmentX(0.0F);
        jProgressBar1.setMaximumSize(new java.awt.Dimension(10000, 16));
        jProgressBar1.setMinimumSize(new java.awt.Dimension(100, 16));
        jProgressBar1.setPreferredSize(new java.awt.Dimension(100, 16));
        getContentPane().add(jProgressBar1);

        fileMenu.setText("File");
        jMenuBar1.add(fileMenu);

        jMenuEdit.setText("Edit");

        jMenuItemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyUtils.CONTROL_OR_CMD_MASK));
        jMenuItemCopy.setText("Copy");
        jMenuEdit.add(jMenuItemCopy);

        jMenuBar1.add(jMenuEdit);

        getDModel().getController().addView((axoloti.swingui.target.TargetMenu)jMenuBoard);
        jMenuBoard.setText("Board");
        jMenuBar1.add(jMenuBoard);
        jMenuBar1.add(windowMenu1);

        helpMenu1.setText("Help");
        jMenuBar1.add(helpMenu1);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearActionPerformed
        jTextPaneLog.setText("");
    }//GEN-LAST:event_jButtonClearActionPerformed

    private void jCheckBoxConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxConnectActionPerformed
        if (!jCheckBoxConnect.isSelected()) {
            IConnection connection = getDModel().getConnection();
            if (connection != null) {
                connection.disconnect();
            }
        } else {
            jCheckBoxConnect.setEnabled(false);
            boolean success = CConnection.getConnection().connect(null);
        }
    }//GEN-LAST:event_jCheckBoxConnectActionPerformed



    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        quit();
    }//GEN-LAST:event_formWindowClosing

    private void jTablePatchesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTablePatchesMouseClicked
        if (evt.getClickCount() == 2) {
            int selectedRow = jTablePatches.getSelectedRow();
            if (selectedRow >= 0) {
                List<ILivePatch> patches = model.getPatchList();
                ILivePatch patch = patches.get(selectedRow);
                IPatchCB cbs = patch.getCallbacks();
                cbs.openEditor();
            }
        }
    }//GEN-LAST:event_jTablePatchesMouseClicked

    public void openPatchFromURL() {
        String url = JOptionPane.showInputDialog(this, "Enter URL:");
        if (url == null) {
            return;
        }
        try {
            InputStream input = new URL(url).openStream();
            String name = url.substring(url.lastIndexOf('/') + 1, url.length());
            PatchViewSwing.openPatch(name, input);
        } catch (MalformedURLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "Invalid URL {0}\n{1}", new Object[]{url, ex});
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "Unable to open URL {0}\n{1}", new Object[]{url, ex});
        }
    }

    public void createNewPatch() {
        PatchModel patchModel = new PatchModel();
        AbstractDocumentRoot documentRoot = new AbstractDocumentRoot();
        patchModel.setDocumentRoot(documentRoot);
        PatchController patchController = patchModel.getController();
        PatchFrame pf = new PatchFrame(patchModel);
        patchController.addView(pf);
        pf.setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private axoloti.swingui.menus.FileMenu fileMenu;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private axoloti.swingui.menus.HelpMenu helpMenu1;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JCheckBox jCheckBoxConnect;
    private javax.swing.JLabel jLabelCPUID;
    private javax.swing.JLabel jLabelFirmwareID;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JLabel jLabelSDCardPresent;
    private javax.swing.JLabel jLabelVolt33;
    private javax.swing.JLabel jLabelVolt50;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuBoard;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenuItem jMenuItemCopy;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPaneLog;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTablePatches;
    private javax.swing.JTextPane jTextPaneLog;
    private axoloti.swingui.menus.WindowMenu windowMenu1;
    // End of variables declaration//GEN-END:variables

    private void showConnectDisconnect(boolean connect) {
        jCheckBoxConnect.setSelected(connect);
        jCheckBoxConnect.setEnabled(true);
        if (!connect) {
            setCpuID(null);
            jLabelVolt50.setText(" ");
            v5000c = 0;
            vdd00c = 0;
            jLabelSDCardPresent.setText(" ");
        }
    }

    public void quit() {
        if (DocumentWindowList.askCloseAll()) {
            return;
        }

        Preferences.getPreferences().savePrefs();
        dispose();
        System.exit(0);
    }

    private void setCpuID(String cpuId) {
        if (cpuId == null) {
            jLabelCPUID.setText(" ");
        } else {
            String name = Preferences.getPreferences().getBoardName(cpuId);
            if (name == null) {
                jLabelCPUID.setText("Cpu ID = " + cpuId);
            } else {
                jLabelCPUID.setText("Cpu ID = " + cpuId + " ( " + name + " ) ");
            }
        }
    }

    void setFirmwareID(String firmwareId) {
        String linkFwId = getDModel().getFirmwareLinkID();
        if (!firmwareId.equals(linkFwId)) {
            if (!TargetModel.getTargetModel().getWarnedAboutFWCRCMismatch()) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "Firmware CRC mismatch! Please flash the firmware first! " + "Hardware firmware CRC = {0} <> Software CRC = {1}", new Object[]{firmwareId, linkFwId});
                TargetModel.getTargetModel().setWarnedAboutFWCRCMismatch(true);
            }
        }
    }

    private int v5000c = 0;
    private int vdd00c = 0;
    private boolean pwarn = false;

    private void setVoltages(float v50, float vdd, boolean warning) {
        int v5000 = (int) (v50 * 100.0f);
        int vdd00 = (int) (vdd * 100.0f);
        boolean upd = false;
        if (((v5000c - v5000) > 1) || ((v5000 - v5000c) > 1)) {
            v5000c = v5000;
            upd = true;
        }
        if (((vdd00c - vdd00) > 1) || ((vdd00 - vdd00c) > 1)) {
            vdd00c = vdd00;
            upd = true;
        }
        if (upd) {
            jLabelVolt50.setText(String.format(" 5V: %.2fV", v5000c / 100.0f));
            jLabelVolt33.setText(String.format("VDD: %.2fV", vdd00c / 100.0f));
        }

        if (warning != pwarn) {
            Color c;
            if (warning) {
                c = Theme.getCurrentTheme().Error_Text;
            } else {
                c = Theme.getCurrentTheme().Normal_Text;
            }
            jLabelVolt50.setForeground(c);
            jLabelVolt33.setForeground(c);
        }
        pwarn = warning;
    }

    public void interactiveFirmwareUpdate() {
        int s = JOptionPane.showConfirmDialog(this,
                "Firmware CRC mismatch detected!\n"
                + "Do you want to update the firmware?\n"
                + "This process will cause a disconnect, "
                + "the leds will blink for a minute, "
                + "do not interrupt until the leds "
                + "stop blinking.\n"
                + "When the leds stop blinking, you can connect again.\n",
                "Firmware update...",
                JOptionPane.YES_NO_OPTION);
        if (s == 0) {
            String pname = Axoloti.getFirmwareFilename();
            try {
                getDModel().flashUsingSDRam(pname);
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.startsWith("open:")) {
            String fn = cmd.substring(5);
            if (fn.endsWith(".axb")) {
                PatchBank.openPatchBankEditor(new File(fn));
            } else if (fn.endsWith(".axp") || fn.endsWith(".axs") || fn.endsWith(".axh")) {
                PatchViewSwing.openPatch(new File(fn));
            }
        }
    }

    public ThemeEditor getThemeEditor() {
        return themeEditor;
    }


    public void setGrabFocusOnSevereErrors(boolean b) {
        bGrabFocusOnSevereErrors = b;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (TargetModel.RTINFO.is(evt)) {
            TargetRTInfo rtinfo = (TargetRTInfo)evt.getNewValue();
            if (rtinfo != null) {
                setVoltages(rtinfo.v50, rtinfo.vdd, rtinfo.voltageAlert);
            }
        } else if (TargetModel.CONNECTION.is(evt)) {
            IConnection connection = (IConnection)evt.getNewValue();
            boolean isConneced = evt.getNewValue() != null;
            showConnectDisconnect(isConneced);
            if (connection != null) {
                setCpuID(connection.getTargetProfile().getCPUSerialString());
                setFirmwareID(connection.getFWID());
            } else {
                setCpuID(null);
            }
        } else if (TargetModel.FIRMWARE_LINK_ID.is(evt)) {
            String linkFwId = (String)evt.getNewValue();
            jLabelFirmwareID.setText("Firmware ID = " + linkFwId);
            Logger.getLogger(MainFrame.class.getName()).log(Level.INFO, "Link to firmware CRC {0}", linkFwId);
            TargetModel.getTargetModel().setWarnedAboutFWCRCMismatch(false);
        } else if (TargetModel.HAS_SDCARD.is(evt)) {
            Boolean b = (Boolean) evt.getNewValue();
            if (b) {
                jLabelSDCardPresent.setText("SDCard mounted");
            } else {
                jLabelSDCardPresent.setText("no SDCard");
            }
        } else if (TargetModel.PATCHLIST.is(evt)) {
            ((AbstractTableModel) jTablePatches.getModel()).fireTableDataChanged();
        }
    }

    private IProgressReporter getProgressReporter() {

        jProgressBar1.setMinimum(0);
        jProgressBar1.setMaximum(16384);
        jProgressBar1.setStringPainted(true);
        return new IProgressReporter() {
            @Override
            public void setProgress(float value) {
                jProgressBar1.setValue((int) (value * 16384));
            }

            @Override
            public void setProgressIndeterminate() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void setNote(String note) {
                jProgressBar1.setEnabled(true);
                jProgressBar1.setString(note);
            }

            @Override
            public void setReady() {
                jProgressBar1.setEnabled(false);
                jProgressBar1.setString("ready");
                jProgressBar1.setValue(0);
            }
        };

    }

}
