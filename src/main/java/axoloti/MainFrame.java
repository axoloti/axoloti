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
package axoloti;

import static axoloti.Axoloti.FIRMWARE_DIR;
import static axoloti.Axoloti.HOME_DIR;
import static axoloti.Axoloti.RELEASE_DIR;
import static axoloti.Axoloti.RUNTIME_DIR;
import axoloti.dialogs.AxolotiRemoteControl;
import axoloti.dialogs.FileManagerFrame;
import axoloti.dialogs.KeyboardFrame;
import axoloti.dialogs.MidiRouting;
import axoloti.dialogs.PatchBank;
import axoloti.dialogs.PreferencesFrame;
import axoloti.dialogs.TJFrame;
import axoloti.dialogs.ThemeEditor;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.object.AxoObjects;
import axoloti.utils.AxolotiLibrary;
import axoloti.utils.KeyUtils;
import axoloti.utils.Preferences;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.swing.BoundedRangeModel;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import qcmds.QCmdProcessor;

/**
 *
 * @author Johannes Taelman
 */
public final class MainFrame extends TJFrame implements ActionListener {

    static public AxoObjects axoObjects;
    public static MainFrame mainframe;

    ThemeEditor themeEditor;
    KeyboardFrame keyboard;
    FileManagerFrame filemanager;
    AxolotiRemoteControl remote;
    MidiRouting midirouting;

    private Thread qcmdprocessorThread;
    static public Cursor transparentCursor;
    private boolean bGrabFocusOnSevereErrors = true;

    private boolean doAutoScroll = true;

    /**
     * Creates new form MainFrame
     *
     * @param args command line arguments
     */
    public MainFrame(String args[], TargetController controller) {
        super(controller);
        initComponents();
        jLabelVoltages.setSize(jLabelVoltages.getPreferredSize());
        fileMenu.initComponents();

        transparentCursor = getToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(), null);

        mainframe = this;

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
            BoundedRangeModel brm = jScrollPaneLog.getVerticalScrollBar().getModel();

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
            BoundedRangeModel brm = jScrollPaneLog.getVerticalScrollBar().getModel();

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
                            jTextPaneLog.getDocument().insertString(jTextPaneLog.getDocument().getEndPosition().getOffset(),
                                    txt + "\n", styleSevere);
                            if (bGrabFocusOnSevereErrors) {
                                MainFrame.this.toFront();
                            }
                        } else if (lr.getLevel() == Level.WARNING) {
                            jTextPaneLog.getDocument().insertString(jTextPaneLog.getDocument().getEndPosition().getOffset(),
                                    txt + "\n", styleWarning);
                        } else {
                            jTextPaneLog.getDocument().insertString(jTextPaneLog.getDocument().getEndPosition().getOffset(),
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
        doLayout();

        TargetController targetController = getController();

        keyboard = new KeyboardFrame(targetController);
        targetController.addView(keyboard);
        //piano.setAlwaysOnTop(true);
        keyboard.setTitle("Keyboard");
        keyboard.setVisible(false);

        filemanager = new FileManagerFrame(targetController);
        targetController.addView(filemanager);
        filemanager.setTitle("File Manager");
        filemanager.setVisible(false);

        themeEditor = new ThemeEditor();
        themeEditor.setTitle("Theme Editor");
        themeEditor.setVisible(false);

        remote = new AxolotiRemoteControl(targetController);
        targetController.addView(remote);
        remote.setTitle("Remote");
        remote.setVisible(false);

        midirouting = new MidiRouting(targetController);
        targetController.addView(midirouting);
        midirouting.setTitle("MIDI Routing");
        midirouting.setVisible(false);

        if (!TestDir(HOME_DIR, true)) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "Home directory is invalid:{0}, does it exist?, can it be written to?", System.getProperty(Axoloti.HOME_DIR));
        }

        if (!TestDir(RELEASE_DIR, false)) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "Release directory is invalid:{0}, does it exist?", System.getProperty(Axoloti.RELEASE_DIR));
        }
        if (!TestDir(RUNTIME_DIR, false)) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "Runtime directory is invalid:{0}, is the runtime installed? correctly?", System.getProperty(Axoloti.RUNTIME_DIR));
        }

        if (!TestDir(FIRMWARE_DIR, false)) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "Firmware directory is invalid:{0}, does it exist?", System.getProperty(Axoloti.FIRMWARE_DIR));
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

                    QCmdProcessor qcmdprocessor = QCmdProcessor.getQCmdProcessor();
                    qcmdprocessorThread = new Thread(qcmdprocessor);
                    qcmdprocessorThread.setName("QCmdProcessor");
                    qcmdprocessorThread.start();

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
                    axoObjects = new AxoObjects();
                    axoObjects.LoadAxoObjects();

                    if (!Axoloti.isFailSafeMode()) {
                        boolean success = CConnection.GetConnection().connect(null);
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
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // wait for objects be loaded
                                    if (axoObjects.LoaderThread.isAlive()) {
                                        EventQueue.invokeLater(this);
                                    } else {
                                        PatchViewSwing.OpenPatch(f);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        EventQueue.invokeLater(r);
                    }
                } else if (arg.endsWith(".axo")) {
                    // NOP for AXO at the moment
                }
            }
        }

        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "Known issues: \n"
                + "* removing objects with parameter-on-parent broken\n"
                + "* modulations are broken\n"
                + "* zombie objects broken\n"
                + "* modules are broken\n"
                + "* create patch/patcher, add object, set parameter on parent, modify on-parent parameter value, undo, undo, redo, redo\n");
        
        controller.addView(this);
    }

    static boolean TestDir(String var, boolean write) {
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
        jLabelVoltages = new javax.swing.JLabel();
        jLabelPatch = new javax.swing.JLabel();
        jLabelSDCardPresent = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0), new java.awt.Dimension(32767, 0));
        jPanel5 = new javax.swing.JPanel();
        jPanel4 = new axoloti.dialogs.TargetRTInfo(getController());
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        jScrollPaneLog = new javax.swing.JScrollPane();
        jTextPaneLog = new javax.swing.JTextPane();
        jPanelProgress = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        jLabelProgress = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new axoloti.menus.FileMenu();
        jMenuEdit = new javax.swing.JMenu();
        jMenuItemCopy = new javax.swing.JMenuItem();
        jMenuBoard = new axoloti.menus.TargetMenu(getController());
        windowMenu1 = new axoloti.menus.WindowMenu();
        helpMenu1 = new axoloti.menus.HelpMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Axoloti");
        setMinimumSize(new java.awt.Dimension(200, 200));
        setPreferredSize(new java.awt.Dimension(355, 325));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.PAGE_AXIS));

        jPanel2.setPreferredSize(new java.awt.Dimension(272, 0));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.PAGE_AXIS));

        jLabelIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/axoloti_icon.png"))); // NOI18N
        jPanel3.add(jLabelIcon);

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

        jCheckBoxConnect.setText("Connect");
        jCheckBoxConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxConnectActionPerformed(evt);
            }
        });
        jPanel1.add(jCheckBoxConnect);

        jLabelCPUID.setText("CPUID");
        jPanel1.add(jLabelCPUID);

        jLabelFirmwareID.setText("FirmwareID");
        jPanel1.add(jLabelFirmwareID);

        jLabelVoltages.setText("5V : 5.00V VDD : 3.30V ");
        jPanel1.add(jLabelVoltages);

        jLabelPatch.setText("patch");
        jPanel1.add(jLabelPatch);

        jLabelSDCardPresent.setText("no SDCard");
        jPanel1.add(jLabelSDCardPresent);

        jPanel2.add(jPanel1);
        jPanel2.add(filler3);

        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.PAGE_AXIS));

        getController().addView((axoloti.dialogs.TargetRTInfo)jPanel4);
        jPanel5.add(jPanel4);
        jPanel5.add(filler2);

        jPanel2.add(jPanel5);

        getContentPane().add(jPanel2);

        jScrollPaneLog.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPaneLog.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPaneLog.setPreferredSize(new java.awt.Dimension(32767, 32767));

        jTextPaneLog.setEditable(false);
        jScrollPaneLog.setViewportView(jTextPaneLog);

        getContentPane().add(jScrollPaneLog);

        jPanelProgress.setMaximumSize(new java.awt.Dimension(605, 16));
        jPanelProgress.setLayout(new javax.swing.BoxLayout(jPanelProgress, javax.swing.BoxLayout.LINE_AXIS));

        jProgressBar1.setAlignmentX(0.0F);
        jProgressBar1.setMaximumSize(new java.awt.Dimension(100, 16));
        jProgressBar1.setMinimumSize(new java.awt.Dimension(100, 16));
        jProgressBar1.setPreferredSize(new java.awt.Dimension(100, 16));
        jPanelProgress.add(jProgressBar1);
        jPanelProgress.add(filler1);

        jLabelProgress.setFocusable(false);
        jLabelProgress.setMaximumSize(new java.awt.Dimension(500, 14));
        jLabelProgress.setPreferredSize(new java.awt.Dimension(150, 14));
        jPanelProgress.add(jLabelProgress);

        getContentPane().add(jPanelProgress);

        fileMenu.setText("File");
        jMenuBar1.add(fileMenu);

        jMenuEdit.setText("Edit");

        jMenuItemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyUtils.CONTROL_OR_CMD_MASK));
        jMenuItemCopy.setText("Copy");
        jMenuEdit.add(jMenuItemCopy);

        jMenuBar1.add(jMenuEdit);

        getController().addView((axoloti.menus.TargetMenu)jMenuBoard);
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
            IConnection connection = getController().getModel().getConnection();
            if (connection != null) {
                connection.disconnect();
            }
        } else {
            QCmdProcessor.getQCmdProcessor().Panic();
            boolean success = CConnection.GetConnection().connect(null);
        }
    }//GEN-LAST:event_jCheckBoxConnectActionPerformed

    PreferencesFrame pp;
// usually we run all tests, as many may fail for same reason and you want
// a list of all affected files, but if you want to stop on first failure, flip this flag
    public static boolean stopOnFirstFail = false;

    public boolean runAllTests() {
        boolean r1 = runPatchTests();
        if (!r1 && stopOnFirstFail) {
            return r1;
        }
        boolean r2 = runObjectTests();
        if (!r2 && stopOnFirstFail) {
            return r2;
        }
        return r1 && r2;
    }

    public boolean runPatchTests() {
        AxolotiLibrary fLib = Preferences.getPreferences().getLibrary(AxolotiLibrary.FACTORY_ID);
        if (fLib == null) {
            return false;
        }
        File testDirName = new File("test");
        if (!testDirName.isDirectory()) {
            testDirName.mkdir();
        }
        testDirName = new File("test/" + fLib.getId());
        if (!testDirName.isDirectory()) {
            testDirName.mkdir();
        }
        testDirName = new File("test/" + fLib.getId() + "/patches/");
        if (!testDirName.isDirectory()) {
            testDirName.mkdir();
        }
        return runTestDir(new File(fLib.getLocalLocation() + "patches"), "test/" + fLib.getId());
    }

    public boolean runObjectTests() {
        AxolotiLibrary fLib = Preferences.getPreferences().getLibrary(AxolotiLibrary.FACTORY_ID);
        if (fLib == null) {
            return false;
        }
        File testDirName = new File("test");
        if (!testDirName.isDirectory()) {
            testDirName.mkdir();
        }
        testDirName = new File("test/" + fLib.getId());
        if (!testDirName.isDirectory()) {
            testDirName.mkdir();
        }
        testDirName = new File("test/" + fLib.getId() + "/objects/");
        if (!testDirName.isDirectory()) {
            testDirName.mkdir();
        }
        return runTestDir(new File(fLib.getLocalLocation() + "objects"), "test/" + fLib.getId());
    }

    public boolean runFileTest(String patchName) {
        return runTestDir(new File(patchName), "");
    }

    private boolean runTestDir(File f, String targetPath) {
        if (!f.exists()) {
            return true;
        }
        if (f.isDirectory()) {
            targetPath += File.separator + f.getName();
            File testDirName = new File(targetPath);
            if (!testDirName.isDirectory()) {
                testDirName.mkdir();
            }
            File[] files = f.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File f, String name) {
                    File t = new File(f + File.separator + name);
                    if (t.isDirectory()) {
                        return true;
                    }

                    if (name.length() < 4) {
                        return false;
                    }
                    String extension = name.substring(name.length() - 4);
                    boolean b = (extension.equals(".axh") || extension.equals(".axp"));
                    return b;
                }
            });
            for (File s : files) {
                if (!runTestDir(s, targetPath) && stopOnFirstFail) {
                    return false;
                }
            }
            return true;
        }

        return runTestCompile(f, targetPath);
    }

    private boolean runTestCompile(File f, String destinationPath) {
        Logger.getLogger(MainFrame.class.getName()).log(Level.INFO, "testing {0}", f.getPath());

        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        try {
            boolean status;
            PatchModel patchModel = serializer.read(PatchModel.class, f);
            PatchController patchController = new PatchController(patchModel, null, null);
            /* fixme: null */
            String basename = f.getName();
            File testDirName = new File(destinationPath);
            if (!testDirName.isDirectory()) {
                testDirName.mkdir();
            }
            String outFileName = destinationPath + File.separator + basename.substring(0, basename.lastIndexOf('.'));
            patchController.WriteCode(outFileName);
            return true;
        } catch (Exception ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "COMPILE FAILED: " + f.getPath(), ex);
            return false;
        }
    }

    public boolean runFileUpgrade(String patchName) {
        return runUpgradeDir(new File(patchName));
    }

    private boolean runUpgradeDir(File f) {
        if (!f.exists()) {
            return true;
        }
        if (f.isDirectory()) {
            File[] files = f.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File f, String name) {
                    File t = new File(f + File.separator + name);
                    if (t.isDirectory()) {
                        return true;
                    }

                    if (name.length() < 4) {
                        return false;
                    }
                    String extension = name.substring(name.length() - 4);
                    boolean b = (extension.equals(".axh") || extension.equals(".axp") || extension.equals(".axs"));
                    return b;
                }
            });
            for (File s : files) {
                if (!runUpgradeDir(s) && stopOnFirstFail) {
                    return false;
                }
            }
            return true;
        }

        return runUpgradeFile(f);
    }

    private boolean runUpgradeFile(File f) {
        Logger.getLogger(MainFrame.class.getName()).log(Level.INFO, "upgrading {0}", f.getPath());

        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        try {
            boolean status;
            PatchModel patchModel = serializer.read(PatchModel.class, f);
            PatchController patchController = new PatchController(patchModel, null, null); /* fixme: null */
            PatchView patchView = Preferences.getPreferences().getPatchView(patchController);
            PatchFrame patchFrame = new PatchFrame(patchController, QCmdProcessor.getQCmdProcessor());
            patchController.addView(patchFrame);
            status = patchModel.save(f);
            if (status == false) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "UPGRADING FAILED: {0}", f.getPath());
            }
            return status;
        } catch (Exception ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "UPGRADING FAILED: " + f.getPath(), ex);
            return false;
        }
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        Quit();
    }//GEN-LAST:event_formWindowClosing

    public void OpenURL() {
        String url = JOptionPane.showInputDialog(this, "Enter URL:");
        if (url == null) {
            return;
        }
        try {
            InputStream input = new URL(url).openStream();
            String name = url.substring(url.lastIndexOf("/") + 1, url.length());
            PatchViewSwing.OpenPatch(name, input);
        } catch (MalformedURLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "Invalid URL {0}\n{1}", new Object[]{url, ex});
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "Unable to open URL {0}\n{1}", new Object[]{url, ex});
        }
    }

    public void NewPatch() {
        PatchModel patchModel = new PatchModel();
        AbstractDocumentRoot documentRoot = new AbstractDocumentRoot();
        PatchController patchController = new PatchController(patchModel, documentRoot, null);
        PatchFrame pf = new PatchFrame(patchController, QCmdProcessor.getQCmdProcessor());
        patchController.addView(pf);
        pf.setVisible(true);
    }

    public void NewBank() {
        PatchBank b = new PatchBank();
        b.setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private axoloti.menus.FileMenu fileMenu;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private axoloti.menus.HelpMenu helpMenu1;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JCheckBox jCheckBoxConnect;
    private javax.swing.JLabel jLabelCPUID;
    private javax.swing.JLabel jLabelFirmwareID;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JLabel jLabelPatch;
    private javax.swing.JLabel jLabelProgress;
    private javax.swing.JLabel jLabelSDCardPresent;
    private javax.swing.JLabel jLabelVoltages;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuBoard;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenuItem jMenuItemCopy;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelProgress;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPaneLog;
    private javax.swing.JTextPane jTextPaneLog;
    private axoloti.menus.WindowMenu windowMenu1;
    // End of variables declaration//GEN-END:variables

    public void SetProgressValue(int i) {
        jProgressBar1.setValue(i);
    }

    public void SetProgressMessage(String s) {
        jLabelProgress.setText(s);
    }

    private void ShowConnectDisconnect(boolean connect) {
        jCheckBoxConnect.setSelected(connect);

        if (!connect) {
            setCpuID(null);
            jLabelVoltages.setText(" ");
            jLabelPatch.setText(" ");
            v5000c = 0;
            vdd00c = 0;
            jLabelPatch.setText("");
            jLabelSDCardPresent.setText(" ");
        }
    }

    public void Quit() {
        while (!DocumentWindowList.GetList().isEmpty()) {
            if (DocumentWindowList.GetList().get(0).askClose()) {
                return;
            }
        }
        Preferences.getPreferences().SavePrefs();
        if (DocumentWindowList.GetList().isEmpty()) {
            System.exit(0);
        }
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

    public boolean WarnedAboutFWCRCMismatch = false;

    void setFirmwareID(String firmwareId) {
        String linkFwId = getController().getModel().getFirmwareLinkID();
        if (!firmwareId.equals(linkFwId)) {
            if (!WarnedAboutFWCRCMismatch) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "Firmware CRC mismatch! Please flash the firmware first! " + "Hardware firmware CRC = {0} <> Software CRC = {1}", new Object[]{firmwareId, linkFwId});
                WarnedAboutFWCRCMismatch = true;
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        interactiveFirmwareUpdate();
                    }
                });
            }
        }
    }

    private void showPatchIndex(int patchIndex) {
        String s;
        switch (patchIndex) {
            case -1:
                s = "running /start.bin";
                break;
            case -2:
                s = "running flash patch";
                break;
            case -3:
                s = "running sdcard .bin file";
                break;
            case -4:
                s = "running live patch";
                break;
            case -5:
                s = " ";
                break;
            default:
                s = "running #" + patchIndex;
        }
        jLabelPatch.setText(s);
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
            jLabelVoltages.setText(String.format("5V: %.2fV VDD: %.2fV", v5000c / 100.0f, vdd00c / 100.0f));
        }

        if (warning != pwarn) {
            if (warning) {
                jLabelVoltages.setForeground(Theme.getCurrentTheme().Error_Text);
            } else {
                jLabelVoltages.setForeground(Theme.getCurrentTheme().Normal_Text);
            }
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
            String fname = System.getProperty(Axoloti.FIRMWARE_DIR) + "/flasher/flasher_build/flasher";
            String pname = System.getProperty(Axoloti.FIRMWARE_DIR) + "/build/axoloti.bin";
            getController().getModel().flashUsingSDRam(fname, pname);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.startsWith("open:")) {
            String fn = cmd.substring(5);
            if (fn.endsWith(".axb")) {
                PatchBank.OpenBank(new File(fn));
            } else if (fn.endsWith(".axp") || fn.endsWith(".axs") || fn.endsWith(".axh")) {
                PatchViewSwing.OpenPatch(new File(fn));
            }
        }
    }

    public FileManagerFrame getFilemanager() {
        return filemanager;
    }

    public ThemeEditor getThemeEditor() {
        return themeEditor;
    }

    public AxolotiRemoteControl getRemote() {
        return remote;
    }

    public MidiRouting getMidiRouting() {
        return midirouting;
    }
    
    public KeyboardFrame getKeyboard() {
        return keyboard;
    }

    public void SetGrabFocusOnSevereErrors(boolean b) {
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
            ShowConnectDisconnect(isConneced);
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
            WarnedAboutFWCRCMismatch = false;
        } else if (TargetModel.HAS_SDCARD.is(evt)) {
            Boolean b = (Boolean) evt.getNewValue();
            if (b) {
                jLabelSDCardPresent.setText("SDCard mounted");
            } else {
                jLabelSDCardPresent.setText("no SDCard");
            }
        } else if (TargetModel.PATCHINDEX.is(evt)) {
            int i = (Integer)evt.getNewValue();
            showPatchIndex(i);
        }
    }

}
