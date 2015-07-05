/**
 * Copyright (C) 2013, 2014, 2015 Johannes Taelman
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

import axoloti.dialogs.AboutFrame;
import axoloti.dialogs.AxolotiRemoteControl;
import axoloti.dialogs.FileManagerFrame;
import axoloti.dialogs.KeyboardFrame;
import axoloti.dialogs.PreferencesFrame;
import axoloti.object.AxoObjects;
import axoloti.usb.Usb;
import axoloti.utils.Constants;
import axoloti.utils.FirmwareID;
import axoloti.utils.Preferences;
import generatedobjects.GeneratedObjects;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import qcmds.QCmdBringToDFUMode;
import qcmds.QCmdPing;
import qcmds.QCmdProcessor;
import qcmds.QCmdStart;
import qcmds.QCmdStop;
import qcmds.QCmdUploadFWSDRam;
import qcmds.QCmdUploadPatch;

/**
 *
 * @author Johannes Taelman
 */
public class MainFrame extends javax.swing.JFrame implements ActionListener {

    static public Preferences prefs = Preferences.LoadPreferences();
    static public AxoObjects axoObjects;
    public static MainFrame mainframe;
    boolean even = false;
    ArrayList<PatchGUI> patches = new ArrayList<PatchGUI>();
    String LinkFirmwareID;
    String TargetFirmwareID;
    KeyboardFrame keyboard;
    FileManagerFrame filemanager;
    AxolotiRemoteControl remote;
    QCmdProcessor qcmdprocessor;
    Thread qcmdprocessorThread;
    static public Cursor transparentCursor;
    AxolotiMidiInput midiInput;

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        setIconImage(new ImageIcon(getClass().getResource("/resources/axoloti_icon.png")).getImage());

        transparentCursor = getToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(), null);

        mainframe = this;

        updateLinkFirmwareID();

        qcmdprocessor = new QCmdProcessor();
        qcmdprocessorThread = new Thread(qcmdprocessor);
        qcmdprocessorThread.setName("QCmdProcessor");
        qcmdprocessorThread.start();

        final Style styleSevere = jTextPaneLog.addStyle("severe", null);
        final Style styleFine = jTextPaneLog.addStyle("fine", null);
        StyleConstants.setForeground(styleSevere, Color.red);
        StyleConstants.setForeground(styleFine, Color.black);

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
                        if (lr.getLevel() == Level.SEVERE) {
                            jTextPaneLog.getDocument().insertString(jTextPaneLog.getDocument().getEndPosition().getOffset(), lr.getMessage() + "\n", styleSevere);
                            MainFrame.this.toFront();
                        } else {
                            jTextPaneLog.getDocument().insertString(jTextPaneLog.getDocument().getEndPosition().getOffset(), lr.getMessage() + "\n", styleFine);
                        }
                    } catch (BadLocationException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    jTextPaneLog.setCaretPosition(jTextPaneLog.getText().length());
                    jTextPaneLog.validate();
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
        //piano.setAlwaysOnTop(true);
        filemanager.setTitle("File Manager");
        filemanager.setVisible(false);

        remote = new AxolotiRemoteControl();
        remote.setTitle("Remote");
        remote.setVisible(false);

        if (!prefs.getExpertMode()) {
            jMenuRegenerateObjects.setVisible(false);
            jMenuAutoTest.setVisible(false);
            jMenuItemRefreshFWID.setVisible(false);
        }
        PopulateLibraryMenu(jMenuLibrary);

        axoObjects = new AxoObjects();
        axoObjects.LoadAxoObjects();
        midiInput = new AxolotiMidiInput();
        initMidiInput(prefs.getMidiInputDevice());

        ShowDisconnect();
    }

    void PopulateLibraryMenu(JMenu parent) {
        JMenu ptut = new JMenu("tutorials");
        PopulateLibraryMenu(ptut, "patches/tutorials",".axp");
        parent.add(ptut);
        JMenu pdemos = new JMenu("demos");
        PopulateLibraryMenu(pdemos, "patches/demos",".axp");
        parent.add(pdemos);
        JMenu phelps = new JMenu("help");
        PopulateLibraryMenu(phelps, "objects",".axh");
        parent.add(phelps);
    }

    void PopulateLibraryMenu(JMenu parent, String path,String ext) {
        File dir = new File(path);
        final String extension=ext;
        for (File subdir : dir.listFiles(new java.io.FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        })) {
            JMenu fm = new JMenu(subdir.getName());
            PopulateLibraryMenu(fm, subdir.getPath(),extension);
            if(fm.getItemCount()>0) {
                parent.add(fm);
            }
        }
        for (String fn : dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (name.endsWith(extension));
            }
        })) {
            String fn2 = fn.substring(0, fn.length() - 4);
            JMenuItem fm = new JMenuItem(fn2);
            fm.setActionCommand("open:" + path + File.separator + fn);
            fm.addActionListener(this);
            parent.add(fm);
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

        jButtonClear = new javax.swing.JButton();
        jScrollPaneLog = new javax.swing.JScrollPane();
        jTextPaneLog = new javax.swing.JTextPane();
        jPanelProgress = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        jLabelProgress = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jCheckBoxConnect = new javax.swing.JCheckBox();
        jLabelCPUID = new javax.swing.JLabel();
        jLabelFirmwareID = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuNew = new javax.swing.JMenuItem();
        jMenuOpen = new javax.swing.JMenuItem();
        jMenuLibrary = new javax.swing.JMenu();
        recentFileMenu1 = new axoloti.menus.RecentFileMenu();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuReloadObjects = new javax.swing.JMenuItem();
        jMenuRegenerateObjects = new javax.swing.JMenuItem();
        jMenuAutoTest = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItemPreferences = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuQuit = new javax.swing.JMenuItem();
        jMenuEdit = new javax.swing.JMenu();
        jMenuItemCopy = new javax.swing.JMenuItem();
        jMenuBoard = new javax.swing.JMenu();
        jMenuItemSelectCom = new javax.swing.JMenuItem();
        jMenuItemFConnect = new javax.swing.JMenuItem();
        jMenuItemFDisconnect = new javax.swing.JMenuItem();
        jMenuItemPing = new javax.swing.JMenuItem();
        jMenuItemPanic = new javax.swing.JMenuItem();
        jMenuFirmware = new javax.swing.JMenu();
        jMenuItemFCompile = new javax.swing.JMenuItem();
        jMenuItemFlashSDR = new javax.swing.JMenuItem();
        jMenuItemEnterDFU = new javax.swing.JMenuItem();
        jMenuItemFlashDFU = new javax.swing.JMenuItem();
        jMenuItemRefreshFWID = new javax.swing.JMenuItem();
        jMenuWindow = new javax.swing.JMenu();
        jMenuHelp = new javax.swing.JMenu();
        jMenuHelpContents = new javax.swing.JMenuItem();
        jMenuAbout = new javax.swing.JMenuItem();
        jMenuCommunity = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Axoloti");
        setMinimumSize(new java.awt.Dimension(200, 200));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jButtonClear.setText("Clear");
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearActionPerformed(evt);
            }
        });

        jScrollPaneLog.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTextPaneLog.setEditable(false);
        jScrollPaneLog.setViewportView(jTextPaneLog);

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

        jCheckBoxConnect.setText("Connect");
        jCheckBoxConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxConnectActionPerformed(evt);
            }
        });

        jLabelCPUID.setText("CPUID");

        jLabelFirmwareID.setText("FirmwareID");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelCPUID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabelFirmwareID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jCheckBoxConnect, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jCheckBoxConnect)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelCPUID)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelFirmwareID))
        );

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/axoloti_icon.png"))); // NOI18N

        jMenuFile.setText("File");

        jMenuNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    jMenuNew.setText("New");
    jMenuNew.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuNewActionPerformed(evt);
        }
    });
    jMenuFile.add(jMenuNew);

    jMenuOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
jMenuOpen.setText("Open...");
jMenuOpen.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuOpenActionPerformed(evt);
    }
    });
    jMenuFile.add(jMenuOpen);

    jMenuLibrary.setText("Library");
    jMenuFile.add(jMenuLibrary);

    recentFileMenu1.setText("Open recent");
    jMenuFile.add(recentFileMenu1);
    jMenuFile.add(jSeparator2);

    jMenuReloadObjects.setText("Reload Objects");
    jMenuReloadObjects.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuReloadObjectsActionPerformed(evt);
        }
    });
    jMenuFile.add(jMenuReloadObjects);

    jMenuRegenerateObjects.setText("Regenerate Objects");
    jMenuRegenerateObjects.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuRegenerateObjectsActionPerformed(evt);
        }
    });
    jMenuFile.add(jMenuRegenerateObjects);

    jMenuAutoTest.setText("Automated test");
    jMenuAutoTest.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuAutoTestActionPerformed(evt);
        }
    });
    jMenuFile.add(jMenuAutoTest);
    jMenuFile.add(jSeparator3);

    jMenuItemPreferences.setText("Preferences...");
    jMenuItemPreferences.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemPreferencesActionPerformed(evt);
        }
    });
    jMenuFile.add(jMenuItemPreferences);
    jMenuFile.add(jSeparator1);

    jMenuQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
jMenuQuit.setText("Quit");
jMenuQuit.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuQuitActionPerformed(evt);
    }
    });
    jMenuFile.add(jMenuQuit);

    jMenuBar1.add(jMenuFile);

    jMenuEdit.setText("Edit");

    jMenuItemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
jMenuItemCopy.setText("Copy");
jMenuEdit.add(jMenuItemCopy);

jMenuBar1.add(jMenuEdit);

jMenuBoard.setText("Board");

jMenuItemSelectCom.setText("Select device...");
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

    jMenuFirmware.setText("Firmware");

    jMenuItemFCompile.setText("Compile");
    jMenuItemFCompile.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemFCompileActionPerformed(evt);
        }
    });
    jMenuFirmware.add(jMenuItemFCompile);

    jMenuItemFlashSDR.setText("Flash (using sdram)");
    jMenuItemFlashSDR.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemFlashSDRActionPerformed(evt);
        }
    });
    jMenuFirmware.add(jMenuItemFlashSDR);

    jMenuItemEnterDFU.setText("Enter DFU");
    jMenuItemEnterDFU.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemEnterDFUActionPerformed(evt);
        }
    });
    jMenuFirmware.add(jMenuItemEnterDFU);

    jMenuItemFlashDFU.setText("Flash (using DFU)");
    jMenuItemFlashDFU.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemFlashDFUActionPerformed(evt);
        }
    });
    jMenuFirmware.add(jMenuItemFlashDFU);

    jMenuItemRefreshFWID.setText("Refresh firmware ID");
    jMenuItemRefreshFWID.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItemRefreshFWIDActionPerformed(evt);
        }
    });
    jMenuFirmware.add(jMenuItemRefreshFWID);

    jMenuBoard.add(jMenuFirmware);

    jMenuBar1.add(jMenuBoard);

    jMenuWindow.setText("Window");
    jMenuWindow.addMenuListener(new javax.swing.event.MenuListener() {
        public void menuSelected(javax.swing.event.MenuEvent evt) {
            jMenuWindowMenuSelected(evt);
        }
        public void menuDeselected(javax.swing.event.MenuEvent evt) {
            jMenuWindowMenuDeselected(evt);
        }
        public void menuCanceled(javax.swing.event.MenuEvent evt) {
        }
    });
    jMenuBar1.add(jMenuWindow);

    jMenuHelp.setText("Help");

    jMenuHelpContents.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
    jMenuHelpContents.setText("Help Contents");
    jMenuHelpContents.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuHelpContentsActionPerformed(evt);
        }
    });
    jMenuHelp.add(jMenuHelpContents);

    jMenuAbout.setText("About...");
    jMenuAbout.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuAboutActionPerformed(evt);
        }
    });
    jMenuHelp.add(jMenuAbout);

    jMenuCommunity.setText("Community website");
    jMenuCommunity.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuCommunityActionPerformed(evt);
        }
    });
    jMenuHelp.add(jMenuCommunity);

    jMenuBar1.add(jMenuHelp);

    setJMenuBar(jMenuBar1);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jScrollPaneLog)
        .addComponent(jPanelProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 88, Short.MAX_VALUE)
            .addComponent(jButtonClear)
            .addContainerGap())
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jButtonClear))
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jScrollPaneLog, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
            .addGap(0, 0, 0)
            .addComponent(jPanelProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearActionPerformed
        jTextPaneLog.setText("");
        doLayout();
        repaint();
    }//GEN-LAST:event_jButtonClearActionPerformed

    private void jMenuAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuAboutActionPerformed
        AboutFrame.aboutFrame.setVisible(true);
    }//GEN-LAST:event_jMenuAboutActionPerformed

    private void jMenuHelpContentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuHelpContentsActionPerformed
        try {
            File f = new File("doc/index.html");
            Desktop.getDesktop().browse(f.toURI());
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuHelpContentsActionPerformed

    private void jMenuWindowMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_jMenuWindowMenuSelected
        WindowMenu.PopulateWindowMenu(jMenuWindow);
    }//GEN-LAST:event_jMenuWindowMenuSelected

    private void jMenuWindowMenuDeselected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_jMenuWindowMenuDeselected
        jMenuWindow.removeAll();
    }//GEN-LAST:event_jMenuWindowMenuDeselected

    private void jMenuItemPanicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPanicActionPerformed
        qcmdprocessor.Panic();
    }//GEN-LAST:event_jMenuItemPanicActionPerformed

    private void jMenuItemPingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPingActionPerformed
        qcmdprocessor.AppendToQueue(new QCmdPing());
    }//GEN-LAST:event_jMenuItemPingActionPerformed

    private void jMenuItemFDisconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFDisconnectActionPerformed
        qcmdprocessor.serialconnection.disconnect();
    }//GEN-LAST:event_jMenuItemFDisconnectActionPerformed

    private void jMenuItemFConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFConnectActionPerformed
        qcmdprocessor.serialconnection.connect();
    }//GEN-LAST:event_jMenuItemFConnectActionPerformed

    private void jMenuItemSelectComActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSelectComActionPerformed
        qcmdprocessor.serialconnection.SelectPort();
    }//GEN-LAST:event_jMenuItemSelectComActionPerformed

    private void jMenuQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuQuitActionPerformed
        Quit();
    }//GEN-LAST:event_jMenuQuitActionPerformed

    private void jMenuOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuOpenActionPerformed
        OpenPatch();
    }//GEN-LAST:event_jMenuOpenActionPerformed

    private void jMenuNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuNewActionPerformed
        NewPatch();
    }//GEN-LAST:event_jMenuNewActionPerformed

    private void jCheckBoxConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxConnectActionPerformed
        if (!jCheckBoxConnect.isSelected()) {
            qcmdprocessor.serialconnection.disconnect();
        } else {
            qcmdprocessor.Panic();
            boolean success = qcmdprocessor.serialconnection.connect();
            if (!success) {
                ShowDisconnect();
            } else {
                qcmdprocessor.AppendToQueue(new QCmdStop());
            }
        }
    }//GEN-LAST:event_jCheckBoxConnectActionPerformed

    private void jMenuReloadObjectsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuReloadObjectsActionPerformed
        axoObjects.LoadAxoObjects();
    }//GEN-LAST:event_jMenuReloadObjectsActionPerformed

    PreferencesFrame pp;
    private void jMenuItemPreferencesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPreferencesActionPerformed
        if (pp == null) {
            pp = new PreferencesFrame(MainFrame.prefs);
        }
        pp.setState(java.awt.Frame.NORMAL);
        pp.setVisible(true);
    }//GEN-LAST:event_jMenuItemPreferencesActionPerformed

    private void jMenuRegenerateObjectsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuRegenerateObjectsActionPerformed
        GeneratedObjects.WriteAxoObjects();
        jMenuReloadObjectsActionPerformed(evt);
    }//GEN-LAST:event_jMenuRegenerateObjectsActionPerformed

    private void jMenuAutoTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuAutoTestActionPerformed
        File testPatchDir = new File("patches/tests");
        for (File f : testPatchDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String extension = name.substring(name.length() - 4);
                return (extension.equals(".axp"));
            }
        })) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.INFO, "loading " + f.getName());
            Serializer serializer = new Persister();
            try {
                PatchGUI patch1 = serializer.read(PatchGUI.class, f);
                PatchFrame pf = new PatchFrame(patch1, qcmdprocessor);
                patch1.PostContructor();
                pf.UpdateConnectStatus();
                patch1.setFileNamePath(f.getPath());
                pf.setVisible(true);
                patches.add(patch1);
                patch1.WriteCode();
                qcmdprocessor.WaitQueueFinished();
                Thread.sleep(500);
                patch1.Compile();
                qcmdprocessor.WaitQueueFinished();
                pf.Close();
                Thread.sleep(2500);
            } catch (Exception ex) {
                Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jMenuAutoTestActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        Quit();
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItemRefreshFWIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRefreshFWIDActionPerformed
        updateLinkFirmwareID();
    }//GEN-LAST:event_jMenuItemRefreshFWIDActionPerformed

    private void jMenuItemFlashDFUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFlashDFUActionPerformed
        if (Usb.isDFUDeviceAvailable()) {
            qcmdprocessor.AppendToQueue(new qcmds.QCmdStop());
            qcmdprocessor.AppendToQueue(new qcmds.QCmdDisconnect());
            qcmdprocessor.AppendToQueue(new qcmds.QCmdFlashDFU());
        } else {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "No devices in DFU mode detected. To bring Axoloti Core in DFU mode, remove power from Axoloti Core, and power it up while holding button S1. The USB port needs to be connected with this computer too...");
        }
    }//GEN-LAST:event_jMenuItemFlashDFUActionPerformed

    private void jMenuItemFlashSDRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFlashSDRActionPerformed
        String fname = Constants.firmwaredir + "/flasher/flasher_build/flasher.bin";
        File f = new File(fname);
        if (f.canRead()) {
            qcmdprocessor.AppendToQueue(new QCmdUploadFWSDRam());
            qcmdprocessor.AppendToQueue(new QCmdUploadPatch(f));
            qcmdprocessor.AppendToQueue(new QCmdStart(null));
        } else {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "can't read flasher, please compile firmware! (file: " + fname + " )");
        }
    }//GEN-LAST:event_jMenuItemFlashSDRActionPerformed

    private void jMenuItemFCompileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFCompileActionPerformed
        qcmdprocessor.AppendToQueue(new qcmds.QCmdCompileFirmware());
    }//GEN-LAST:event_jMenuItemFCompileActionPerformed

    private void jMenuItemEnterDFUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEnterDFUActionPerformed
        qcmdprocessor.AppendToQueue(new QCmdBringToDFUMode());
    }//GEN-LAST:event_jMenuItemEnterDFUActionPerformed

    private void jMenuCommunityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuCommunityActionPerformed
        try {
            Desktop.getDesktop().browse(new URI("http://community.axoloti.com"));
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuCommunityActionPerformed

    public void NewPatch() {
        PatchGUI patch1 = new PatchGUI();
        PatchFrame pf = new PatchFrame(patch1, qcmdprocessor);
        patch1.PostContructor();
        patch1.setFileNamePath("untitled");
        patches.add(patch1);
        pf.setVisible(true);
    }

    public void OpenPatch() {
        final JFileChooser fc = new JFileChooser(prefs.getCurrentFileDirectory());
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Axoloti Files", "axp", "axh","axs"));
        fc.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.getName().endsWith("axp")) {
                    return true;
                }
                return false;
            }

            @Override
            public String getDescription() {
                return "Axoloti Patch";
            }
        });
        fc.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.getName().endsWith("axh")) {
                    return true;
                }
                return false;
            }

            @Override
            public String getDescription() {
                return "Axoloti Help";
            }
        });
        fc.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.getName().endsWith("axs")) {
                    return true;
                }
                return false;
            }

            @Override
            public String getDescription() {
                return "Axoloti Subpatch";
            }
        });

        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            prefs.setCurrentFileDirectory(fc.getCurrentDirectory().getPath());
            prefs.SavePrefs();
            File f = fc.getSelectedFile();
            OpenPatch(f);
        }
    }

    public void OpenPatch(File f) {
        Serializer serializer = new Persister();
        try {
            PatchGUI patch1 = serializer.read(PatchGUI.class, f);
            PatchFrame pf = new PatchFrame(patch1, qcmdprocessor);
            patch1.setFileNamePath(f.getAbsolutePath());
            patch1.PostContructor();
            pf.UpdateConnectStatus();
            patch1.setFileNamePath(f.getPath());
            pf.setVisible(true);
            patches.add(patch1);
            MainFrame.prefs.addRecentFile(f.getAbsolutePath());
        } catch (Exception ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JCheckBox jCheckBoxConnect;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelCPUID;
    private javax.swing.JLabel jLabelFirmwareID;
    private javax.swing.JLabel jLabelProgress;
    private javax.swing.JMenuItem jMenuAbout;
    private javax.swing.JMenuItem jMenuAutoTest;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuBoard;
    private javax.swing.JMenuItem jMenuCommunity;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuFirmware;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenuItem jMenuHelpContents;
    private javax.swing.JMenuItem jMenuItemCopy;
    private javax.swing.JMenuItem jMenuItemEnterDFU;
    private javax.swing.JMenuItem jMenuItemFCompile;
    private javax.swing.JMenuItem jMenuItemFConnect;
    private javax.swing.JMenuItem jMenuItemFDisconnect;
    private javax.swing.JMenuItem jMenuItemFlashDFU;
    private javax.swing.JMenuItem jMenuItemFlashSDR;
    private javax.swing.JMenuItem jMenuItemPanic;
    private javax.swing.JMenuItem jMenuItemPing;
    private javax.swing.JMenuItem jMenuItemPreferences;
    private javax.swing.JMenuItem jMenuItemRefreshFWID;
    private javax.swing.JMenuItem jMenuItemSelectCom;
    private javax.swing.JMenu jMenuLibrary;
    private javax.swing.JMenuItem jMenuNew;
    private javax.swing.JMenuItem jMenuOpen;
    private javax.swing.JMenuItem jMenuQuit;
    private javax.swing.JMenuItem jMenuRegenerateObjects;
    private javax.swing.JMenuItem jMenuReloadObjects;
    private javax.swing.JMenu jMenuWindow;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelProgress;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPaneLog;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JTextPane jTextPaneLog;
    private axoloti.menus.RecentFileMenu recentFileMenu1;
    // End of variables declaration//GEN-END:variables

    public void SetProgressValue(int i) {
        jProgressBar1.setValue(i);
    }

    public void SetProgressMessage(String s) {
        jLabelProgress.setText(s);
    }

    public void ShowDisconnect() {
        ShowConnectDisconnect(false);
    }

    public void ShowConnect() {
        ShowConnectDisconnect(true);
    }

    void ShowConnectDisconnect(boolean connect) {
        for (Patch p : patches) {
            if (connect) {
                p.patchframe.ShowConnect();
            } else {
                p.patchframe.ShowDisconnect();
            }
        }
        jCheckBoxConnect.setSelected(connect);
        jMenuItemEnterDFU.setEnabled(connect);
        jMenuItemFlashSDR.setEnabled(connect);
        jMenuItemFDisconnect.setEnabled(connect);

        jMenuItemFConnect.setEnabled(!connect);
        jMenuItemSelectCom.setEnabled(!connect);

        if (!connect) {
            setCpuID(null);
        }
    }

    void Quit() {
        while (!patches.isEmpty()) {
            if (patches.get(0).patchframe.AskClose()) {
                break;
            }
        }
        prefs.SavePrefs();
        if (patches.isEmpty()) {
            System.exit(0);
        }
    }

    void setCpuID(String cpuId) {
        if (cpuId == null) {
            jLabelCPUID.setText(" ");
        } else {
            jLabelCPUID.setText("Cpu ID = " + cpuId);
        }
    }

    public void updateLinkFirmwareID() {
        LinkFirmwareID = FirmwareID.getFirmwareID();
        //TargetFirmwareID = LinkFirmwareID;
        jLabelFirmwareID.setText("Firmware ID = " + LinkFirmwareID);
        Logger.getLogger(MainFrame.class.getName()).info("Link to firmware CRC " + LinkFirmwareID);
    }

//    boolean isFirmwareUpgrading = false;
    void setFirmwareID(String firmwareId) {
        if (firmwareId.equals(TargetFirmwareID)) {
            return;
        }
        TargetFirmwareID = firmwareId;
        if (!firmwareId.equals(this.LinkFirmwareID)) {
            Logger.getLogger(AxoObjects.class.getName()).severe("Firmware CRC mismatch! Please flash the firmware first! Target firmware CRC = " + firmwareId);
            LinkFirmwareID = firmwareId;
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
            OpenPatch(new File(fn));
        }
    }

    public void initMidiInput(String midiInputDevice) {
        midiInput.start(midiInputDevice);
    }
}
