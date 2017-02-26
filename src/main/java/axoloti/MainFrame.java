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
import axoloti.dialogs.PatchBank;
import axoloti.dialogs.PreferencesFrame;
import axoloti.dialogs.ThemeEditor;
import axoloti.object.AxoObjects;
import axoloti.usb.Usb;
import axoloti.utils.AxolotiLibrary;
import axoloti.utils.FirmwareID;
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
import javax.swing.ImageIcon;
import javax.swing.JMenu;
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
import qcmds.QCmdBringToDFUMode;
import qcmds.QCmdCompilePatch;
import qcmds.QCmdPing;
import qcmds.QCmdProcessor;
import qcmds.QCmdStartFlasher;
import qcmds.QCmdStartMounter;
import qcmds.QCmdStop;
import qcmds.QCmdUploadFWSDRam;
import qcmds.QCmdUploadPatch;

/**
 *
 * @author Johannes Taelman
 */
public final class MainFrame extends javax.swing.JFrame implements ActionListener, ConnectionStatusListener, SDCardMountStatusListener {

    static public Preferences prefs = Preferences.LoadPreferences();
    static public AxoObjects axoObjects;
    public static MainFrame mainframe;
    boolean even = false;
    String LinkFirmwareID;
    String TargetFirmwareID;
    KeyboardFrame keyboard;
    FileManagerFrame filemanager;
    ThemeEditor themeEditor;
    AxolotiRemoteControl remote;
    QCmdProcessor qcmdprocessor;
    Thread qcmdprocessorThread;
    static public Cursor transparentCursor;
    private final String[] args;
    JMenu favouriteMenu;
    boolean bGrabFocusOnSevereErrors = true;

    private boolean doAutoScroll = true;

    /**
     * Creates new form MainFrame
     *
     * @param args command line arguments
     */
    public MainFrame(String args[]) {
        this.args = args;
        initComponents();
        fileMenu.initComponents();
        setIconImage(new ImageIcon(getClass().getResource("/resources/axoloti_icon.png")).getImage());

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

        keyboard = new KeyboardFrame();
        //piano.setAlwaysOnTop(true);
        keyboard.setTitle("Keyboard");
        keyboard.setVisible(false);

        filemanager = new FileManagerFrame();
        filemanager.setTitle("File Manager");
        filemanager.setVisible(false);

        themeEditor = new ThemeEditor();
        themeEditor.setTitle("Theme Editor");
        themeEditor.setVisible(false);

        remote = new AxolotiRemoteControl();
        remote.setTitle("Remote");
        remote.setVisible(false);

        if (!prefs.getExpertMode()) {
            jMenuItemRefreshFWID.setVisible(false);
        }

        jMenuItemEnterDFU.setVisible(Axoloti.isDeveloper());
        jMenuItemFlashSDR.setVisible(Axoloti.isDeveloper());
        jMenuItemFCompile.setVisible(Axoloti.isDeveloper());
        jDevSeparator.setVisible(Axoloti.isDeveloper());

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

                    updateLinkFirmwareID();

                    qcmdprocessor = QCmdProcessor.getQCmdProcessor();
                    qcmdprocessorThread = new Thread(qcmdprocessor);
                    qcmdprocessorThread.setName("QCmdProcessor");
                    qcmdprocessorThread.start();
                    USBBulkConnection.GetConnection().addConnectionStatusListener(MainFrame.this);
                    USBBulkConnection.GetConnection().addSDCardMountStatusListener(MainFrame.this);

                    // user library, ask user if they wish to upgrade, or do manuall
                    // this allows them the opportunity to manually backup their files!
                    AxolotiLibrary ulib = prefs.getLibrary(AxolotiLibrary.USER_LIBRARY_ID);
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
                    ulib = prefs.getLibrary(AxolotiLibrary.FACTORY_ID);
                    if (ulib != null) {
                        String cb = ulib.getCurrentBranch();
                        if (!cb.equalsIgnoreCase(ulib.getBranch())) {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.INFO, "Current factory library does not match correct version, upgrading {0} -> {1}", new Object[]{cb, ulib.getBranch()});
                            ulib.upgrade();
                        }
                    }

                    if (!Axoloti.isFailSafeMode()) {
                        for (AxolotiLibrary lib : prefs.getLibraries()) {
                            if (lib.isAutoSync() && lib.getEnabled()) {
                                lib.sync();
                            }
                        }
                    }
                    for (AxolotiLibrary lib : prefs.getLibraries()) {
                        lib.reportStatus();
                    }
                    axoObjects = new AxoObjects();
                    axoObjects.LoadAxoObjects();

                    ShowDisconnect();
                    if (!Axoloti.isFailSafeMode()) {
                        boolean success = USBBulkConnection.GetConnection().connect();
                        if (success) {
                            ShowConnect();
                        }
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
                                        PatchGUI.OpenPatch(f);
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

    void flashUsingSDRam(String fname_flasher, String pname) {
        updateLinkFirmwareID();
        File f = new File(fname_flasher);
        File p = new File(pname);
        if (f.canRead()) {
            if (p.canRead()) {
                qcmdprocessor.AppendToQueue(new QCmdStop());
                qcmdprocessor.AppendToQueue(new QCmdUploadFWSDRam(p));
                qcmdprocessor.AppendToQueue(new QCmdUploadPatch(f));
                qcmdprocessor.AppendToQueue(new QCmdStartFlasher());
            } else {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "can''t read firmware, please compile firmware! (file: {0} )", pname);
            }
        } else {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "can''t read flasher, please compile firmware! (file: {0} )", fname_flasher);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
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
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
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
        jMenuBoard = new javax.swing.JMenu();
        jMenuItemSelectCom = new javax.swing.JMenuItem();
        jMenuItemFConnect = new javax.swing.JMenuItem();
        jMenuItemFDisconnect = new javax.swing.JMenuItem();
        jMenuItemPing = new javax.swing.JMenuItem();
        jMenuItemPanic = new javax.swing.JMenuItem();
        jMenuItemMount = new javax.swing.JMenuItem();
        jMenuFirmware = new javax.swing.JMenu();
        jMenuItemFlashDefault = new javax.swing.JMenuItem();
        jMenuItemFlashDFU = new javax.swing.JMenuItem();
        jMenuItemRefreshFWID = new javax.swing.JMenuItem();
        jDevSeparator = new javax.swing.JPopupMenu.Separator();
        jMenuItemFCompile = new javax.swing.JMenuItem();
        jMenuItemEnterDFU = new javax.swing.JMenuItem();
        jMenuItemFlashSDR = new javax.swing.JMenuItem();
        windowMenu1 = new axoloti.menus.WindowMenu();
        helpMenu1 = new axoloti.menus.HelpMenu();

        jLabel1.setText("jLabel1");

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

        jLabelVoltages.setText("volt");
        jPanel1.add(jLabelVoltages);

        jLabelPatch.setText("patch");
        jPanel1.add(jLabelPatch);

        jLabelSDCardPresent.setText("no SDCard");
        jPanel1.add(jLabelSDCardPresent);

        jPanel2.add(jPanel1);
        jPanel2.add(filler3);

        getContentPane().add(jPanel2);

        jScrollPaneLog.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPaneLog.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

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

        jMenuBoard.setText("Board");

        jMenuItemSelectCom.setText("Select Device...");
        jMenuItemSelectCom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSelectComActionPerformed(evt);
            }
        });
        jMenuBoard.add(jMenuItemSelectCom);

        jMenuItemFConnect.setText("Connect");
        jMenuItemFConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFConnectActionPerformed(evt);
            }
        });
        jMenuBoard.add(jMenuItemFConnect);

        jMenuItemFDisconnect.setText("Disconnect");
        jMenuItemFDisconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFDisconnectActionPerformed(evt);
            }
        });
        jMenuBoard.add(jMenuItemFDisconnect);

        jMenuItemPing.setText("Ping");
        jMenuItemPing.setEnabled(false);
        jMenuItemPing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPingActionPerformed(evt);
            }
        });
        jMenuBoard.add(jMenuItemPing);

        jMenuItemPanic.setText("Panic");
        jMenuItemPanic.setEnabled(false);
        jMenuItemPanic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPanicActionPerformed(evt);
            }
        });
        jMenuBoard.add(jMenuItemPanic);

        jMenuItemMount.setText("Enter card reader mode (disconnects editor)");
        jMenuItemMount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMountActionPerformed(evt);
            }
        });
        jMenuBoard.add(jMenuItemMount);

        jMenuFirmware.setText("Firmware");

        jMenuItemFlashDefault.setText("Flash");
        jMenuItemFlashDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFlashDefaultActionPerformed(evt);
            }
        });
        jMenuFirmware.add(jMenuItemFlashDefault);

        jMenuItemFlashDFU.setText("Flash (Rescue)");
        jMenuItemFlashDFU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFlashDFUActionPerformed(evt);
            }
        });
        jMenuFirmware.add(jMenuItemFlashDFU);

        jMenuItemRefreshFWID.setText("Refresh Firmware ID");
        jMenuItemRefreshFWID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRefreshFWIDActionPerformed(evt);
            }
        });
        jMenuFirmware.add(jMenuItemRefreshFWID);
        jMenuFirmware.add(jDevSeparator);

        jMenuItemFCompile.setText("Compile");
        jMenuItemFCompile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFCompileActionPerformed(evt);
            }
        });
        jMenuFirmware.add(jMenuItemFCompile);

        jMenuItemEnterDFU.setText("Enter Rescue mode");
        jMenuItemEnterDFU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEnterDFUActionPerformed(evt);
            }
        });
        jMenuFirmware.add(jMenuItemEnterDFU);

        jMenuItemFlashSDR.setText("Flash (User)");
        jMenuItemFlashSDR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFlashSDRActionPerformed(evt);
            }
        });
        jMenuFirmware.add(jMenuItemFlashSDR);

        jMenuBoard.add(jMenuFirmware);

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

    private void jMenuItemPanicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPanicActionPerformed
        qcmdprocessor.Panic();
    }//GEN-LAST:event_jMenuItemPanicActionPerformed

    private void jMenuItemPingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPingActionPerformed
        qcmdprocessor.AppendToQueue(new QCmdPing());
    }//GEN-LAST:event_jMenuItemPingActionPerformed

    private void jMenuItemFDisconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFDisconnectActionPerformed
        USBBulkConnection.GetConnection().disconnect();
    }//GEN-LAST:event_jMenuItemFDisconnectActionPerformed

    private void jMenuItemFConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFConnectActionPerformed
        USBBulkConnection.GetConnection().connect();
    }//GEN-LAST:event_jMenuItemFConnectActionPerformed

    private void jMenuItemSelectComActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSelectComActionPerformed
        USBBulkConnection.GetConnection().SelectPort();
    }//GEN-LAST:event_jMenuItemSelectComActionPerformed

    private void jCheckBoxConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxConnectActionPerformed
        if (!jCheckBoxConnect.isSelected()) {
            USBBulkConnection.GetConnection().disconnect();
        } else {
            qcmdprocessor.Panic();
            boolean success = USBBulkConnection.GetConnection().connect();
            if (!success) {
                ShowDisconnect();
            }
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
        AxolotiLibrary fLib = prefs.getLibrary(AxolotiLibrary.FACTORY_ID);
        if (fLib == null) {
            return false;
        }
        return runTestDir(new File(fLib.getLocalLocation() + "patches"));
    }

    public boolean runObjectTests() {
        AxolotiLibrary fLib = prefs.getLibrary(AxolotiLibrary.FACTORY_ID);
        if (fLib == null) {
            return false;
        }
        return runTestDir(new File(fLib.getLocalLocation() + "objects"));
    }

    public boolean runFileTest(String patchName) {
        return runTestDir(new File(patchName));
    }

    private boolean runTestDir(File f) {
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
                    boolean b = (extension.equals(".axh") || extension.equals(".axp"));
                    return b;
                }
            });
            for (File s : files) {
                if (!runTestDir(s) && stopOnFirstFail) {
                    return false;
                }
            }
            return true;
        }

        return runTestCompile(f);
    }

    private boolean runTestCompile(File f) {
        Logger.getLogger(MainFrame.class.getName()).log(Level.INFO, "testing {0}", f.getPath());

        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        try {
            boolean status;
            PatchGUI patch1 = serializer.read(PatchGUI.class, f);
            PatchFrame pf = new PatchFrame(patch1, qcmdprocessor);
            patch1.setFileNamePath(f.getPath());
            patch1.PostContructor();
            patch1.WriteCode();
            qcmdprocessor.WaitQueueFinished();
            Thread.sleep(500);
            QCmdCompilePatch cp = new QCmdCompilePatch(patch1);
            patch1.GetQCmdProcessor().AppendToQueue(cp);
            qcmdprocessor.WaitQueueFinished();
            pf.Close();
            Thread.sleep(2500);
            status = cp.success();
            if (status == false) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "COMPILE FAILED: {0}", f.getPath());
            }
            return status;
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
            PatchGUI patch1 = serializer.read(PatchGUI.class, f);
            PatchFrame pf = new PatchFrame(patch1, qcmdprocessor);
            patch1.setFileNamePath(f.getPath());
            patch1.PostContructor();
            status = patch1.save(f);
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

    private void jMenuItemRefreshFWIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRefreshFWIDActionPerformed
        updateLinkFirmwareID();
    }//GEN-LAST:event_jMenuItemRefreshFWIDActionPerformed

    private void jMenuItemFlashDFUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFlashDFUActionPerformed
        if (Usb.isDFUDeviceAvailable()) {
            updateLinkFirmwareID();
            qcmdprocessor.AppendToQueue(new qcmds.QCmdStop());
            qcmdprocessor.AppendToQueue(new qcmds.QCmdDisconnect());
            qcmdprocessor.AppendToQueue(new qcmds.QCmdFlashDFU());
        } else {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "No devices in DFU mode detected. To bring Axoloti Core in DFU mode, remove power from Axoloti Core, and then connect the micro-USB port to your computer while holding button S1. The LEDs will stay off when in DFU mode.");
        }
    }//GEN-LAST:event_jMenuItemFlashDFUActionPerformed

    private void jMenuItemFlashSDRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFlashSDRActionPerformed
        String fname = System.getProperty(Axoloti.FIRMWARE_DIR) + "/flasher/flasher_build/flasher.bin";
        String pname = System.getProperty(Axoloti.FIRMWARE_DIR) + "/build/axoloti.bin";
        flashUsingSDRam(fname, pname);
    }//GEN-LAST:event_jMenuItemFlashSDRActionPerformed

    private void jMenuItemFCompileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFCompileActionPerformed
        qcmdprocessor.AppendToQueue(new qcmds.QCmdCompileFirmware());
    }//GEN-LAST:event_jMenuItemFCompileActionPerformed

    private void jMenuItemEnterDFUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEnterDFUActionPerformed
        qcmdprocessor.AppendToQueue(new QCmdBringToDFUMode());
    }//GEN-LAST:event_jMenuItemEnterDFUActionPerformed

    private void jMenuItemFlashDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFlashDefaultActionPerformed
        String curFirmwareDir = System.getProperty(Axoloti.FIRMWARE_DIR);
        String sysFirmwareDir = System.getProperty(Axoloti.RELEASE_DIR) + "/firmware";

        if (!curFirmwareDir.equals(sysFirmwareDir)) {
            // if we are using the factory firmware, then we must switch back the firmware dir
            // as this is where we pick up axoloti.elf from when building a patch
            prefs.SetFirmwareDir(sysFirmwareDir);
            prefs.SavePrefs();
        }

        String fname = System.getProperty(Axoloti.FIRMWARE_DIR) + "/flasher/flasher_build/flasher.bin";
        String pname = System.getProperty(Axoloti.FIRMWARE_DIR) + "/build/axoloti.bin";
        flashUsingSDRam(fname, pname);
    }//GEN-LAST:event_jMenuItemFlashDefaultActionPerformed

    private void jMenuItemMountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMountActionPerformed
        String fname = System.getProperty(Axoloti.FIRMWARE_DIR) + "/mounter/mounter_build/mounter.bin";
        File f = new File(fname);
        if (f.canRead()) {
            qcmdprocessor.AppendToQueue(new QCmdStop());
            qcmdprocessor.AppendToQueue(new QCmdUploadPatch(f));
            qcmdprocessor.AppendToQueue(new QCmdStartMounter());
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "will disconnect, unmount sdcard to go back to normal mode (required to connect)");
        } else {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "can''t read mounter firmware, please compile mounter firmware! (file: {0} )", fname);
        }

    }//GEN-LAST:event_jMenuItemMountActionPerformed

    public void OpenURL() {
        String url = JOptionPane.showInputDialog(this, "Enter URL:");
        if (url == null) {
            return;
        }
        try {
            InputStream input = new URL(url).openStream();
            String name = url.substring(url.lastIndexOf("/") + 1, url.length());
            PatchGUI.OpenPatch(name, input);
        } catch (MalformedURLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "Invalid URL {0}\n{1}", new Object[]{url, ex});
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "Unable to open URL {0}\n{1}", new Object[]{url, ex});
        }
    }

    public void NewPatch() {
        PatchGUI patch1 = new PatchGUI();
        PatchFrame pf = new PatchFrame(patch1, qcmdprocessor);
        patch1.PostContructor();
        patch1.setFileNamePath("untitled");
        pf.setVisible(true);
    }

    public void NewBank() {
        PatchBank b = new PatchBank();
        b.setVisible(true);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private axoloti.menus.FileMenu fileMenu;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler3;
    private axoloti.menus.HelpMenu helpMenu1;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JCheckBox jCheckBoxConnect;
    private javax.swing.JPopupMenu.Separator jDevSeparator;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JMenu jMenuFirmware;
    private javax.swing.JMenuItem jMenuItemCopy;
    private javax.swing.JMenuItem jMenuItemEnterDFU;
    private javax.swing.JMenuItem jMenuItemFCompile;
    private javax.swing.JMenuItem jMenuItemFConnect;
    private javax.swing.JMenuItem jMenuItemFDisconnect;
    private javax.swing.JMenuItem jMenuItemFlashDFU;
    private javax.swing.JMenuItem jMenuItemFlashDefault;
    private javax.swing.JMenuItem jMenuItemFlashSDR;
    private javax.swing.JMenuItem jMenuItemMount;
    private javax.swing.JMenuItem jMenuItemPanic;
    private javax.swing.JMenuItem jMenuItemPing;
    private javax.swing.JMenuItem jMenuItemRefreshFWID;
    private javax.swing.JMenuItem jMenuItemSelectCom;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
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

    @Override
    public void ShowDisconnect() {
        ShowConnectDisconnect(false);
    }

    @Override
    public void ShowConnect() {
        ShowConnectDisconnect(true);
    }

    private void ShowConnectDisconnect(boolean connect) {
        jCheckBoxConnect.setSelected(connect);
        jMenuItemFDisconnect.setEnabled(connect);

        jMenuItemFConnect.setEnabled(!connect);
        jMenuItemSelectCom.setEnabled(!connect);

        jMenuItemEnterDFU.setEnabled(connect);
        jMenuItemMount.setEnabled(connect);
        jMenuItemFlashDefault.setEnabled(connect && USBBulkConnection.GetConnection().getTargetProfile().hasSDRAM());
        jMenuItemFlashSDR.setEnabled(connect && USBBulkConnection.GetConnection().getTargetProfile().hasSDRAM());

        if (!connect) {
            setCpuID(null);
            jLabelVoltages.setText(" ");
            jLabelPatch.setText(" ");
            v5000c = 0;
            vdd00c = 0;
            patchIndex = -4;
            jLabelSDCardPresent.setText(" ");
        }
    }

    public void Quit() {
        while (!DocumentWindowList.GetList().isEmpty()) {
            if (DocumentWindowList.GetList().get(0).AskClose()) {
                return;
            }
        }
        prefs.SavePrefs();
        if (DocumentWindowList.GetList().isEmpty()) {
            System.exit(0);
        }
    }

    public void setCpuID(String cpuId) {
        if (cpuId == null) {
            jLabelCPUID.setText(" ");
        } else {
            String name = MainFrame.prefs.getBoardName(cpuId);
            String txt;
            if (name == null) {
                jLabelCPUID.setText("Cpu ID = " + cpuId);
            } else {
                jLabelCPUID.setText("Cpu ID = " + cpuId + " ( " + name + " ) ");
            }
        }
    }

    public void updateLinkFirmwareID() {
        LinkFirmwareID = FirmwareID.getFirmwareID();
        //TargetFirmwareID = LinkFirmwareID;
        jLabelFirmwareID.setText("Firmware ID = " + LinkFirmwareID);
        Logger.getLogger(MainFrame.class.getName()).log(Level.INFO, "Link to firmware CRC {0}", LinkFirmwareID);
        WarnedAboutFWCRCMismatch = false;
    }

    public boolean WarnedAboutFWCRCMismatch = false;

    void setFirmwareID(String firmwareId) {
        TargetFirmwareID = firmwareId;
        if (!firmwareId.equals(this.LinkFirmwareID)) {
            if (!WarnedAboutFWCRCMismatch) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "Firmware CRC mismatch! Please flash the firmware first! " + "Hardware firmware CRC = {0} <> Software CRC = {1}", new Object[]{firmwareId, this.LinkFirmwareID});
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

    private int patchIndex = -3;

    public void showPatchIndex(int index) {
        if (patchIndex != index) {
            patchIndex = index;
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
    }

    private int v5000c = 0;
    private int vdd00c = 0;

    public void setVoltages(float v50, float vdd, boolean warning) {
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

        if (warning) {
            jLabelVoltages.setForeground(Theme.getCurrentTheme().Error_Text);
        } else {
            jLabelVoltages.setForeground(Theme.getCurrentTheme().Normal_Text);
        }
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
            String fname = System.getProperty(Axoloti.FIRMWARE_DIR) + "/flasher/flasher_build/flasher.bin";
            String pname = System.getProperty(Axoloti.FIRMWARE_DIR) + "/build/axoloti.bin";
            flashUsingSDRam(fname, pname);
        }
    }

    public QCmdProcessor getQcmdprocessor() {
        return qcmdprocessor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.startsWith("open:")) {
            String fn = cmd.substring(5);
            if (fn.endsWith(".axb")) {
                PatchBank.OpenBank(new File(fn));
            } else if (fn.endsWith(".axp") || fn.endsWith(".axs") || fn.endsWith(".axh")) {
                PatchGUI.OpenPatch(new File(fn));
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

    public KeyboardFrame getKeyboard() {
        return keyboard;
    }

    public void SetGrabFocusOnSevereErrors(boolean b) {
        bGrabFocusOnSevereErrors = b;
    }

    @Override
    public void ShowSDCardMounted() {
        jLabelSDCardPresent.setText("SDCard mounted");
        jMenuItemMount.setEnabled(true);
    }

    @Override
    public void ShowSDCardUnmounted() {
        jLabelSDCardPresent.setText("no SDCard");
        jMenuItemMount.setEnabled(false);
    }
}
