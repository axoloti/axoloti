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

import axoloti.object.AxoObjectInstance;
import axoloti.object.AxoObjectInstanceAbstract;
import axoloti.object.AxoObjects;
import axoloti.utils.Constants;
import axoloti.utils.KeyUtils;
import components.PresetPanel;
import components.VisibleCablePanel;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import qcmds.QCmdLock;
import qcmds.QCmdProcessor;
import qcmds.QCmdStart;
import qcmds.QCmdStop;
import qcmds.QCmdUploadPatch;

/**
 *
 * @author Johannes Taelman
 */
public class PatchFrame extends javax.swing.JFrame implements DocumentWindow, ConnectionStatusListener, SDCardMountStatusListener {

    /**
     * Creates new form PatchFrame
     */
    PatchGUI patch;

    private PresetPanel presetPanel;
    private VisibleCablePanel visibleCablePanel;

    public PatchFrame(final PatchGUI patch, QCmdProcessor qcmdprocessor) {
        setIconImage(new ImageIcon(getClass().getResource("/resources/axoloti_icon.png")).getImage());
        this.qcmdprocessor = qcmdprocessor;
        initComponents();
        fileMenu1.initComponents();
        this.patch = patch;
        this.patch.patchframe = this;

        presetPanel = new PresetPanel(patch);
        visibleCablePanel = new VisibleCablePanel(patch);
        
        jToolbarPanel.add(presetPanel);
        jToolbarPanel.add(new javax.swing.Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 32767)));
        jToolbarPanel.add(visibleCablePanel);

        jScrollPane1.setViewportView(patch.Layers);
        jScrollPane1.getVerticalScrollBar().setUnitIncrement(Constants.Y_GRID / 2);
        jScrollPane1.getHorizontalScrollBar().setUnitIncrement(Constants.X_GRID / 2);

        JMenuItem menuItem = new JMenuItem(new DefaultEditorKit.CutAction());
        menuItem.setText("Cut");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, 
                KeyUtils.CONTROL_OR_CMD_MASK));
        jMenuEdit.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Patch p = patch.GetSelectedObjects();
                if (p.objectinstances.isEmpty()) {
                    getToolkit().getSystemClipboard().setContents(new StringSelection(""), null);
                    return;
                }
                p.PreSerialize();
                Serializer serializer = new Persister();
                try {
                    Clipboard clip = getToolkit().getSystemClipboard();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    serializer.write(p, baos);
                    StringSelection s = new StringSelection(baos.toString());
                    clip.setContents(s, (ClipboardOwner) null);
                    patch.deleteSelectedAxoObjInstances();
                } catch (Exception ex) {
                    Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        menuItem = new JMenuItem(new DefaultEditorKit.CopyAction());
        menuItem.setText("Copy");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, 
                KeyUtils.CONTROL_OR_CMD_MASK));
        jMenuEdit.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Patch p = patch.GetSelectedObjects();
                if (p.objectinstances.isEmpty()) {
                    getToolkit().getSystemClipboard().setContents(new StringSelection(""), null);
                    return;
                }
                p.PreSerialize();
                Serializer serializer = new Persister();
                try {
                    Clipboard clip = getToolkit().getSystemClipboard();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    serializer.write(p, baos);
                    StringSelection s = new StringSelection(baos.toString());
                    clip.setContents(s, (ClipboardOwner) null);
                } catch (Exception ex) {
                    Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        menuItem = new JMenuItem(new DefaultEditorKit.PasteAction());
        menuItem.setText("Paste");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, 
                KeyUtils.CONTROL_OR_CMD_MASK));
        jMenuEdit.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clipboard clip = getToolkit().getSystemClipboard();
                try {
                    patch.paste((String) clip.getData(DataFlavor.stringFlavor), null, false);
                } catch (UnsupportedFlavorException ex) {
                    Logger.getLogger(PatchFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(PatchFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        if (patch.getWindowPos() != null) {
            setBounds(patch.getWindowPos());
        } else {
            Dimension d = patch.GetInitialSize();
            setSize(d);
        }

        if (!MainFrame.prefs.getExpertMode()) {
            jSeparator3.setVisible(false);
            jMenuItemLock.setVisible(false);
            jMenuGenerateAndCompileCode.setVisible(false);
            jMenuGenerateCode.setVisible(false);
            jMenuCompileCode.setVisible(false);
            jMenuUploadCode.setVisible(false);
            jMenuItemLock.setVisible(false);
            jMenuItemUnlock.setVisible(false);
        }
        jMenuPreset.setVisible(false);
        jMenuItemAdjScroll.setVisible(false);
        patch.Layers.requestFocus();
        if (USBBulkConnection.GetConnection().isConnected()) {
            ShowConnect();
        }
        
        this.undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 
                KeyUtils.CONTROL_OR_CMD_MASK));
        this.redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 
                KeyUtils.CONTROL_OR_CMD_MASK | KeyEvent.SHIFT_DOWN_MASK));

        createBufferStrategy(2);
        USBBulkConnection.GetConnection().addConnectionStatusListener(this);
        USBBulkConnection.GetConnection().addSDCardMountStatusListener(this);
    }

    QCmdProcessor qcmdprocessor;

    public void SetLive(boolean b) {
        if (b) {
            jCheckBoxLive.setSelected(true);
            jCheckBoxLive.setEnabled(true);
            jCheckBoxMenuItemLive.setSelected(true);
            jCheckBoxMenuItemLive.setEnabled(true);
            presetPanel.ShowLive(true);
        } else {
            jCheckBoxLive.setSelected(false);
            jCheckBoxLive.setEnabled(true);
            jCheckBoxMenuItemLive.setSelected(false);
            jCheckBoxMenuItemLive.setEnabled(true);
            presetPanel.ShowLive(false);
        }
    }

    void ShowConnect1(boolean status){
        jCheckBoxLive.setEnabled(status);
        jCheckBoxMenuItemLive.setEnabled(status);
        jMenuItemUploadInternalFlash.setEnabled(status);
        jMenuItemUploadSD.setEnabled(status);
        jMenuItemUploadSDStart.setEnabled(status);
    }
    
    @Override
    public void ShowDisconnect() {
        if (patch.IsLocked()) {
            patch.Unlock();
        }
        jCheckBoxLive.setSelected(false);
        jCheckBoxMenuItemLive.setSelected(false);
        ShowConnect1(false);
    }

    @Override
    public void ShowConnect() {
        patch.Unlock();
        jCheckBoxLive.setSelected(false);
        jCheckBoxMenuItemLive.setSelected(false);
        ShowConnect1(true);
    }

    public void ShowCompileFail() {
        jCheckBoxLive.setSelected(false);
        jCheckBoxLive.setEnabled(true);
    }

    public void Close() {
        DocumentWindowList.UnregisterWindow(this);
        USBBulkConnection.GetConnection().removeConnectionStatusListener(this);
        USBBulkConnection.GetConnection().removeSDCardMountStatusListener(this);
        patch.Close();
        dispose();
    }

    @Override
    public boolean AskClose() {
        if (patch.isDirty() && patch.container() == null) {
            Object[] options = {"Save",
                "Don't save",
                "Cancel"};
            int n = JOptionPane.showOptionDialog(this,
                    "Do you want to save changes to " + patch.getFileNamePath() + " ?",
                    "Axoloti asks:",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[2]);
            switch (n) {
                case JOptionPane.YES_OPTION:
                    jMenuSaveActionPerformed(null);
                    Close();
                    return false;
                case JOptionPane.NO_OPTION:
                    Close();
                    return false;
                case JOptionPane.CANCEL_OPTION:
                    return true;
                default:
                    return false;
            }
        } else {
            Close();
            return false;
        }
    }

    public JScrollPane getScrollPane() {
        return this.jScrollPane1;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolbarPanel = new javax.swing.JPanel();
        jCheckBoxLive = new javax.swing.JCheckBox();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0));
        jLabel1 = new javax.swing.JLabel();
        jProgressBarDSPLoad = new javax.swing.JProgressBar();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0));
        jScrollPane1 = new javax.swing.JScrollPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu1 = new axoloti.menus.FileMenu();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuSave = new javax.swing.JMenuItem();
        jMenuSaveAs = new javax.swing.JMenuItem();
        jMenuSaveCopy = new javax.swing.JMenuItem();
        jMenuSaveClip = new javax.swing.JMenuItem();
        jMenuClose = new javax.swing.JMenuItem();
        jMenuEdit = new javax.swing.JMenu();
        undoItem = new javax.swing.JMenuItem();
        redoItem = new javax.swing.JMenuItem();
        jMenuItemDelete = new javax.swing.JMenuItem();
        jMenuItemSelectAll = new javax.swing.JMenuItem();
        jMenuItemAddObj = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuView = new javax.swing.JMenu();
        jMenuItemNotes = new javax.swing.JMenuItem();
        jMenuItemSettings = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jCheckBoxMenuItemCordsInBackground = new javax.swing.JCheckBoxMenuItem();
        jMenuItemAdjScroll = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuPatch = new javax.swing.JMenu();
        jCheckBoxMenuItemLive = new javax.swing.JCheckBoxMenuItem();
        jMenuItemUploadSD = new javax.swing.JMenuItem();
        jMenuItemUploadSDStart = new javax.swing.JMenuItem();
        jMenuItemUploadInternalFlash = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuGenerateAndCompileCode = new javax.swing.JMenuItem();
        jMenuGenerateCode = new javax.swing.JMenuItem();
        jMenuCompileCode = new javax.swing.JMenuItem();
        jMenuUploadCode = new javax.swing.JMenuItem();
        jMenuItemLock = new javax.swing.JMenuItem();
        jMenuItemUnlock = new javax.swing.JMenuItem();
        jMenuPreset = new javax.swing.JMenu();
        jMenuItemClearPreset = new javax.swing.JMenuItem();
        jMenuItemPresetCurrentToInit = new javax.swing.JMenuItem();
        jMenuItemDifferenceToPreset = new javax.swing.JMenuItem();
        windowMenu1 = new axoloti.menus.WindowMenu();
        helpMenu1 = new axoloti.menus.HelpMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                formWindowLostFocus(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jToolbarPanel.setAlignmentX(1.0F);
        jToolbarPanel.setAlignmentY(0.0F);
        jToolbarPanel.setMaximumSize(new java.awt.Dimension(32767, 0));
        jToolbarPanel.setPreferredSize(new java.awt.Dimension(212, 49));
        jToolbarPanel.setLayout(new javax.swing.BoxLayout(jToolbarPanel, javax.swing.BoxLayout.LINE_AXIS));

        jCheckBoxLive.setText("Live");
        jCheckBoxLive.setEnabled(false);
        jCheckBoxLive.setFocusable(false);
        jCheckBoxLive.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jCheckBoxLive.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jCheckBoxLive.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jCheckBoxLive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxLiveActionPerformed(evt);
            }
        });
        jToolbarPanel.add(jCheckBoxLive);

        filler2.setAlignmentX(0.0F);
        jToolbarPanel.add(filler2);

        jLabel1.setText("DSP load ");
        jToolbarPanel.add(jLabel1);

        jProgressBarDSPLoad.setToolTipText("");
        jProgressBarDSPLoad.setAlignmentX(0.0F);
        jProgressBarDSPLoad.setMaximumSize(new java.awt.Dimension(100, 16));
        jProgressBarDSPLoad.setMinimumSize(new java.awt.Dimension(60, 16));
        jProgressBarDSPLoad.setName(""); // NOI18N
        jProgressBarDSPLoad.setPreferredSize(new java.awt.Dimension(100, 16));
        jProgressBarDSPLoad.setStringPainted(true);
        jToolbarPanel.add(jProgressBarDSPLoad);

        filler3.setAlignmentX(0.0F);
        jToolbarPanel.add(filler3);

        getContentPane().add(jToolbarPanel);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setAutoscrolls(true);
        getContentPane().add(jScrollPane1);

        fileMenu1.setText("File");
        fileMenu1.add(jSeparator1);

        jMenuSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyUtils.CONTROL_OR_CMD_MASK));
        jMenuSave.setText("Save");
        jMenuSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuSaveActionPerformed(evt);
            }
        });
        fileMenu1.add(jMenuSave);

        jMenuSaveAs.setText("Save As...");
        jMenuSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuSaveAsActionPerformed(evt);
            }
        });
        fileMenu1.add(jMenuSaveAs);

        jMenuSaveCopy.setText("Save Copy...");
        jMenuSaveCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuSaveCopyActionPerformed(evt);
            }
        });
        fileMenu1.add(jMenuSaveCopy);

        jMenuSaveClip.setText("Save To Clipboard");
        jMenuSaveClip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuSaveClipActionPerformed(evt);
            }
        });
        fileMenu1.add(jMenuSaveClip);

        jMenuClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyUtils.CONTROL_OR_CMD_MASK));
        jMenuClose.setText("Close");
        jMenuClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuCloseActionPerformed(evt);
            }
        });
        fileMenu1.add(jMenuClose);

        jMenuBar1.add(fileMenu1);

        jMenuEdit.setMnemonic('E');
        jMenuEdit.setText("Edit");

        undoItem.setText("Undo");
        undoItem.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                undoItemAncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        undoItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoItemActionPerformed(evt);
            }
        });
        jMenuEdit.add(undoItem);

        redoItem.setText("Redo");
        redoItem.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                redoItemAncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        redoItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoItemActionPerformed(evt);
            }
        });
        jMenuEdit.add(redoItem);

        jMenuItemDelete.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        jMenuItemDelete.setText("Delete");
        jMenuItemDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDeleteActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemDelete);

        jMenuItemSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyUtils.CONTROL_OR_CMD_MASK));
        jMenuItemSelectAll.setText("Select All");
        jMenuItemSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSelectAllActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemSelectAll);

        jMenuItemAddObj.setText("New Object...");
        jMenuItemAddObj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAddObjActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemAddObj);
        jMenuEdit.add(jSeparator4);

        jMenuBar1.add(jMenuEdit);

        jMenuView.setMnemonic('V');
        jMenuView.setText("View");

        jMenuItemNotes.setText("Notes");
        jMenuItemNotes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNotesActionPerformed(evt);
            }
        });
        jMenuView.add(jMenuItemNotes);

        jMenuItemSettings.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyUtils.CONTROL_OR_CMD_MASK));
        jMenuItemSettings.setText("Settings");
        jMenuItemSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSettingsActionPerformed(evt);
            }
        });
        jMenuView.add(jMenuItemSettings);
        jMenuView.add(jSeparator2);

        jCheckBoxMenuItemCordsInBackground.setText("Patch Cords In Background");
        jCheckBoxMenuItemCordsInBackground.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemCordsInBackgroundActionPerformed(evt);
            }
        });
        jMenuView.add(jCheckBoxMenuItemCordsInBackground);

        jMenuItemAdjScroll.setText("Adjust Scroll");
        jMenuItemAdjScroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAdjScrollActionPerformed(evt);
            }
        });
        jMenuView.add(jMenuItemAdjScroll);
        jMenuView.add(jSeparator5);

        jMenuBar1.add(jMenuView);

        jMenuPatch.setMnemonic('P');
        jMenuPatch.setText("Patch");

        jCheckBoxMenuItemLive.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyUtils.CONTROL_OR_CMD_MASK));
        jCheckBoxMenuItemLive.setText("Live");
        jCheckBoxMenuItemLive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemLiveActionPerformed(evt);
            }
        });
        jMenuPatch.add(jCheckBoxMenuItemLive);

        jMenuItemUploadSD.setText("Upload to SDCard");
        jMenuItemUploadSD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemUploadSDActionPerformed(evt);
            }
        });
        jMenuPatch.add(jMenuItemUploadSD);

        jMenuItemUploadSDStart.setText("Upload to SDCard as startup");
        jMenuItemUploadSDStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemUploadSDStartActionPerformed(evt);
            }
        });
        jMenuPatch.add(jMenuItemUploadSDStart);

        jMenuItemUploadInternalFlash.setText("Upload to internal flash");
        jMenuItemUploadInternalFlash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemUploadInternalFlashActionPerformed(evt);
            }
        });
        jMenuPatch.add(jMenuItemUploadInternalFlash);
        jMenuPatch.add(jSeparator3);

        jMenuGenerateAndCompileCode.setText("Generate & Compile code");
        jMenuGenerateAndCompileCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuGenerateAndCompileCodeActionPerformed(evt);
            }
        });
        jMenuPatch.add(jMenuGenerateAndCompileCode);

        jMenuGenerateCode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyUtils.CONTROL_OR_CMD_MASK));
        jMenuGenerateCode.setText("Generate code");
        jMenuGenerateCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuGenerateCodeActionPerformed(evt);
            }
        });
        jMenuPatch.add(jMenuGenerateCode);

        jMenuCompileCode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyUtils.CONTROL_OR_CMD_MASK));
        jMenuCompileCode.setText("Compile code");
        jMenuCompileCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuCompileCodeActionPerformed(evt);
            }
        });
        jMenuPatch.add(jMenuCompileCode);

        jMenuUploadCode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, KeyUtils.CONTROL_OR_CMD_MASK));
        jMenuUploadCode.setText("Upload code");
        jMenuUploadCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuUploadCodeActionPerformed(evt);
            }
        });
        jMenuPatch.add(jMenuUploadCode);

        jMenuItemLock.setText("Lock");
        jMenuItemLock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemLockActionPerformed(evt);
            }
        });
        jMenuPatch.add(jMenuItemLock);

        jMenuItemUnlock.setText("Unlock");
        jMenuItemUnlock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemUnlockActionPerformed(evt);
            }
        });
        jMenuPatch.add(jMenuItemUnlock);

        jMenuBar1.add(jMenuPatch);

        jMenuPreset.setText("Preset");
        jMenuPreset.setEnabled(false);

        jMenuItemClearPreset.setText("Clear current preset");
        jMenuItemClearPreset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemClearPresetActionPerformed(evt);
            }
        });
        jMenuPreset.add(jMenuItemClearPreset);

        jMenuItemPresetCurrentToInit.setText("Copy current state to init");
        jMenuItemPresetCurrentToInit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPresetCurrentToInitActionPerformed(evt);
            }
        });
        jMenuPreset.add(jMenuItemPresetCurrentToInit);

        jMenuItemDifferenceToPreset.setText("Difference between current and init to preset");
        jMenuItemDifferenceToPreset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDifferenceToPresetActionPerformed(evt);
            }
        });
        jMenuPreset.add(jMenuItemDifferenceToPreset);

        jMenuBar1.add(jMenuPreset);
        jMenuBar1.add(windowMenu1);

        helpMenu1.setText("Help");
        jMenuBar1.add(helpMenu1);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBoxLiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxLiveActionPerformed
        if (jCheckBoxLive.isSelected()) {
            if (GoLive()) {
                jCheckBoxLive.setEnabled(false);
            } else {
                jCheckBoxLive.setSelected(false);
            }
        } else {
            qcmdprocessor.AppendToQueue(new QCmdStop());
            patch.Unlock();
        }
    }//GEN-LAST:event_jCheckBoxLiveActionPerformed

    private void jMenuSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuSaveActionPerformed
        String fn = patch.getFileNamePath();
        if ((fn != null) && (!fn.equals("untitled"))) {
            File f = new File(fn);
            patch.setFileNamePath(f.getPath());
            patch.save(f);
        } else {
            jMenuSaveAsActionPerformed(evt);
        }
    }//GEN-LAST:event_jMenuSaveActionPerformed

    File FileChooserSave() {
        final JFileChooser fc = new JFileChooser(MainFrame.prefs.getCurrentFileDirectory());
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(FileUtils.axpFileFilter);
        fc.addChoosableFileFilter(FileUtils.axsFileFilter);
        fc.addChoosableFileFilter(FileUtils.axhFileFilter);
        String fn = patch.getFileNamePath();
        if (fn == null) {
            fn = "untitled";
        }
        File f = new File(fn);
        fc.setSelectedFile(f);

        String ext = "";
        int dot = fn.lastIndexOf('.');
        if (dot > 0 && fn.length() > dot + 3) {
            ext = fn.substring(dot);
        }
        if (ext.equalsIgnoreCase(".axp")) {
            fc.setFileFilter(FileUtils.axpFileFilter);
        } else if (ext.equalsIgnoreCase(".axs")) {
            fc.setFileFilter(FileUtils.axsFileFilter);
        } else if (ext.equalsIgnoreCase(".axh")) {
            fc.setFileFilter(FileUtils.axhFileFilter);
        } else {
            fc.setFileFilter(FileUtils.axpFileFilter);
        }

        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String filterext = ".axp";
            if (fc.getFileFilter() == FileUtils.axpFileFilter) {
                filterext = ".axp";
            } else if (fc.getFileFilter() == FileUtils.axsFileFilter) {
                filterext = ".axs";
            } else if (fc.getFileFilter() == FileUtils.axhFileFilter) {
                filterext = ".axh";
            }

            File fileToBeSaved = fc.getSelectedFile();
            ext = "";
            String fname = fileToBeSaved.getAbsolutePath();
            dot = fname.lastIndexOf('.');
            if (dot > 0 && fname.length() > dot + 3) {
                ext = fname.substring(dot);
            }

            if (!(ext.equalsIgnoreCase(".axp")
                    || ext.equalsIgnoreCase(".axh")
                    || ext.equalsIgnoreCase(".axs"))) {

                fileToBeSaved = new File(fc.getSelectedFile() + filterext);

            } else if (!ext.equals(filterext)) {
                Object[] options = {"Yes",
                    "No"};
                int n = JOptionPane.showOptionDialog(this,
                        "File does not match filter, do you want to change extension to " + filterext + " ?",
                        "Axoloti asks:",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[1]);
                switch (n) {
                    case JOptionPane.YES_OPTION:
                        fileToBeSaved = new File(fname.substring(0, fname.length() - 4) + filterext);
                        break;
                    case JOptionPane.NO_OPTION:
                        return null;
                }
            }

            if (fileToBeSaved.exists()) {
                Object[] options = {"Yes",
                    "No"};
                int n = JOptionPane.showOptionDialog(this,
                        "File exists, do you want to overwrite ?",
                        "Axoloti asks:",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[1]);
                switch (n) {
                    case JOptionPane.YES_OPTION:
                        break;
                    case JOptionPane.NO_OPTION:
                        return null;
                }
            }
            return fileToBeSaved;
        } else {
            return null;
        }
    }

    private void jMenuSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuSaveAsActionPerformed
        File fileToBeSaved = FileChooserSave();
        if (fileToBeSaved != null) {
            patch.setFileNamePath(fileToBeSaved.getPath());
            MainFrame.prefs.setCurrentFileDirectory(fileToBeSaved.getPath());
            patch.save(fileToBeSaved);
        }
    }//GEN-LAST:event_jMenuSaveAsActionPerformed

    private void jMenuItemAdjScrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAdjScrollActionPerformed
        jScrollPane1.setAutoscrolls(true);
        patch.AdjustSize();
    }//GEN-LAST:event_jMenuItemAdjScrollActionPerformed

    private void jCheckBoxMenuItemCordsInBackgroundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemCordsInBackgroundActionPerformed
        patch.SetCordsInBackground(jCheckBoxMenuItemCordsInBackground.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItemCordsInBackgroundActionPerformed

    private void jMenuGenerateCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuGenerateCodeActionPerformed
        patch.WriteCode();
    }//GEN-LAST:event_jMenuGenerateCodeActionPerformed

    private void jMenuCompileCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuCompileCodeActionPerformed
        patch.Compile();
    }//GEN-LAST:event_jMenuCompileCodeActionPerformed

    private void jMenuUploadCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuUploadCodeActionPerformed
        patch.GetQCmdProcessor().SetPatch(null);
        patch.GetQCmdProcessor().AppendToQueue(new QCmdStop());
        patch.GetQCmdProcessor().AppendToQueue(new QCmdUploadPatch());
        patch.GetQCmdProcessor().AppendToQueue(new QCmdStart(patch));
        patch.GetQCmdProcessor().AppendToQueue(new QCmdLock(patch));
    }//GEN-LAST:event_jMenuUploadCodeActionPerformed

    private void jMenuItemLockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemLockActionPerformed
        patch.Lock();
    }//GEN-LAST:event_jMenuItemLockActionPerformed

    private void jMenuItemUnlockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUnlockActionPerformed
        patch.Unlock();
    }//GEN-LAST:event_jMenuItemUnlockActionPerformed

    private void jMenuItemClearPresetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemClearPresetActionPerformed
        patch.ClearCurrentPreset();
    }//GEN-LAST:event_jMenuItemClearPresetActionPerformed

    private void jMenuItemPresetCurrentToInitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPresetCurrentToInitActionPerformed
        patch.CopyCurrentToInit();
    }//GEN-LAST:event_jMenuItemPresetCurrentToInitActionPerformed

    private void jMenuItemDifferenceToPresetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDifferenceToPresetActionPerformed
        patch.DifferenceToPreset();
    }//GEN-LAST:event_jMenuItemDifferenceToPresetActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        AskClose();
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItemDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDeleteActionPerformed
        patch.deleteSelectedAxoObjInstances();
    }//GEN-LAST:event_jMenuItemDeleteActionPerformed

    private void jMenuItemSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSelectAllActionPerformed
        patch.SelectAll();
    }//GEN-LAST:event_jMenuItemSelectAllActionPerformed

    private void jMenuItemNotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNotesActionPerformed
        patch.ShowNotesFrame();
    }//GEN-LAST:event_jMenuItemNotesActionPerformed

    private void jMenuItemSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSettingsActionPerformed
        AxoObjectInstanceAbstract selObj = null;
        ArrayList<AxoObjectInstanceAbstract> oi = patch.objectinstances;
        if(oi != null) {
            for(AxoObjectInstanceAbstract i : oi) {
                if(i.IsSelected() && i instanceof AxoObjectInstance) {
                    selObj = i;
                }
            }
        }
        
        if(selObj!=null) {
            ((AxoObjectInstance) selObj).OpenEditor();
        } else {
            if (patch.settings == null) {
                patch.settings = new PatchSettings();
            }
            patch.settings.showEditor(patch);
        }
    }//GEN-LAST:event_jMenuItemSettingsActionPerformed

    private void jCheckBoxMenuItemLiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemLiveActionPerformed
        if (jCheckBoxMenuItemLive.isSelected()) {
            if (GoLive()) {
                jCheckBoxMenuItemLive.setEnabled(false);
            } else {
                jCheckBoxMenuItemLive.setSelected(false);

            }
        } else {
            qcmdprocessor.AppendToQueue(new QCmdStop());
            patch.Unlock();
        }
    }//GEN-LAST:event_jCheckBoxMenuItemLiveActionPerformed

    private void jMenuItemUploadSDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUploadSDActionPerformed
        patch.UploadToSDCard();
    }//GEN-LAST:event_jMenuItemUploadSDActionPerformed

    private void jMenuItemUploadSDStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUploadSDStartActionPerformed
        patch.UploadToSDCard("/start.bin");
    }//GEN-LAST:event_jMenuItemUploadSDStartActionPerformed

    private void jMenuSaveClipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuSaveClipActionPerformed
        Serializer serializer = new Persister();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
        try {
            serializer.write(patch, baos);
        } catch (Exception ex) {
            Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
        }
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        c.setContents(new StringSelection(baos.toString()), null);
    }//GEN-LAST:event_jMenuSaveClipActionPerformed

    private void jMenuItemUploadInternalFlashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUploadInternalFlashActionPerformed
        patch.WriteCode();
        qcmdprocessor.AppendToQueue(new qcmds.QCmdStop());
        qcmdprocessor.AppendToQueue(new qcmds.QCmdCompilePatch(patch));
        qcmdprocessor.AppendToQueue(new qcmds.QCmdUploadPatch());
        qcmdprocessor.AppendToQueue(new qcmds.QCmdCopyPatchToFlash());
    }//GEN-LAST:event_jMenuItemUploadInternalFlashActionPerformed

    private void jMenuItemAddObjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAddObjActionPerformed
        patch.ShowClassSelector(new Point(20, 20), null, null);
    }//GEN-LAST:event_jMenuItemAddObjActionPerformed

    private void jMenuCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuCloseActionPerformed
        AskClose();
    }//GEN-LAST:event_jMenuCloseActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        DocumentWindowList.RegisterWindow(this);
    }//GEN-LAST:event_formComponentShown

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
        DocumentWindowList.UnregisterWindow(this);
    }//GEN-LAST:event_formComponentHidden

    private void jMenuSaveCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuSaveCopyActionPerformed
        File fileToBeSaved = FileChooserSave();
        if (fileToBeSaved != null) {
            MainFrame.prefs.setCurrentFileDirectory(fileToBeSaved.getPath());
            patch.save(fileToBeSaved);
        }
    }//GEN-LAST:event_jMenuSaveCopyActionPerformed

    private void jMenuGenerateAndCompileCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuGenerateAndCompileCodeActionPerformed
        patch.WriteCode();
        patch.Compile();
    }//GEN-LAST:event_jMenuGenerateAndCompileCodeActionPerformed

    private void undoItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoItemActionPerformed
        patch.undo();
        this.updateUndoRedoEnabled();
    }//GEN-LAST:event_undoItemActionPerformed

    private void redoItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoItemActionPerformed
        patch.redo();
        this.updateUndoRedoEnabled();
    }//GEN-LAST:event_redoItemActionPerformed

    private void undoItemAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_undoItemAncestorAdded
        undoItem.setEnabled(patch.canUndo());
    }//GEN-LAST:event_undoItemAncestorAdded

    private void redoItemAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_redoItemAncestorAdded
        redoItem.setEnabled(patch.canRedo());
    }//GEN-LAST:event_redoItemAncestorAdded

    private void formWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowLostFocus
        getRootPane().setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_formWindowLostFocus

    private boolean GoLive() {
        if (patch.getFileNamePath().endsWith(".axs") || patch.container() != null) {
            Object[] options = {"Yes",
                "No"};

            int n = JOptionPane.showOptionDialog(this,
                    "This is a subpatch intended to be used by a main patch and possibly has no output. \nDo you still want to take it live?",
                    "Axoloti asks:",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]);
            switch (n) {
                case JOptionPane.NO_OPTION:
                    return false;
                case JOptionPane.YES_OPTION:
                    ; // fall thru
            }
        }
        patch.GoLive();
        return true;
    }

    /* write to sdcard...
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private axoloti.menus.FileMenu fileMenu1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private axoloti.menus.HelpMenu helpMenu1;
    private javax.swing.JCheckBox jCheckBoxLive;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemCordsInBackground;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemLive;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuClose;
    private javax.swing.JMenuItem jMenuCompileCode;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenuItem jMenuGenerateAndCompileCode;
    private javax.swing.JMenuItem jMenuGenerateCode;
    private javax.swing.JMenuItem jMenuItemAddObj;
    private javax.swing.JMenuItem jMenuItemAdjScroll;
    private javax.swing.JMenuItem jMenuItemClearPreset;
    private javax.swing.JMenuItem jMenuItemDelete;
    private javax.swing.JMenuItem jMenuItemDifferenceToPreset;
    private javax.swing.JMenuItem jMenuItemLock;
    private javax.swing.JMenuItem jMenuItemNotes;
    private javax.swing.JMenuItem jMenuItemPresetCurrentToInit;
    private javax.swing.JMenuItem jMenuItemSelectAll;
    private javax.swing.JMenuItem jMenuItemSettings;
    private javax.swing.JMenuItem jMenuItemUnlock;
    private javax.swing.JMenuItem jMenuItemUploadInternalFlash;
    private javax.swing.JMenuItem jMenuItemUploadSD;
    private javax.swing.JMenuItem jMenuItemUploadSDStart;
    private javax.swing.JMenu jMenuPatch;
    private javax.swing.JMenu jMenuPreset;
    private javax.swing.JMenuItem jMenuSave;
    private javax.swing.JMenuItem jMenuSaveAs;
    private javax.swing.JMenuItem jMenuSaveClip;
    private javax.swing.JMenuItem jMenuSaveCopy;
    private javax.swing.JMenuItem jMenuUploadCode;
    private javax.swing.JMenu jMenuView;
    private javax.swing.JProgressBar jProgressBarDSPLoad;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPanel jToolbarPanel;
    private javax.swing.JMenuItem redoItem;
    private javax.swing.JMenuItem undoItem;
    private axoloti.menus.WindowMenu windowMenu1;
    // End of variables declaration//GEN-END:variables

    void ShowDSPLoad(int pct) {
        int pv = jProgressBarDSPLoad.getValue();
        if (pct == pv) {
            return;
        }
        if (pct == (pv - 1)) {
            return;
        }
        jProgressBarDSPLoad.setValue(pct);
    }

    @Override
    public JFrame GetFrame() {
        return this;
    }

    @Override
    public File getFile() {
        if (patch.getFileNamePath() == null) {
            return null;
        } else {
            return new File(patch.getFileNamePath());
        }
    }

    public PatchGUI getPatch() {
        return patch;
    }

    ArrayList<DocumentWindow> dwl = new ArrayList<DocumentWindow>();

    @Override
    public ArrayList<DocumentWindow> GetChildDocuments() {
        return dwl;
    }
    
    public void updateUndoRedoEnabled() {
        redoItem.setEnabled(patch.canRedo());
        undoItem.setEnabled(patch.canUndo());
    }

    @Override
    public void ShowSDCardMounted() {
        jMenuItemUploadSD.setEnabled(true);
        jMenuItemUploadSDStart.setEnabled(true);
    }

    @Override
    public void ShowSDCardUnmounted() {
        jMenuItemUploadSD.setEnabled(false);
        jMenuItemUploadSDStart.setEnabled(false);
    }
}
