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
package axoloti.swingui.patch;

import axoloti.FileUtils;
import axoloti.abstractui.DocumentWindow;
import axoloti.abstractui.DocumentWindowList;
import axoloti.abstractui.IAbstractEditor;
import axoloti.abstractui.PatchView;
import axoloti.codegen.patch.PatchViewCodegen;
import axoloti.connection.CConnection;
import axoloti.connection.ConnectionStatusListener;
import axoloti.connection.IConnection;
import axoloti.job.GlobalJobProcessor;
import axoloti.live.patch.PatchViewLive;
import axoloti.mvc.IView;
import axoloti.objectlibrary.AxoObjects;
import axoloti.patch.PatchController;
import axoloti.patch.PatchModel;
import axoloti.patch.PatchViewType;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.preferences.Preferences;
import axoloti.shell.ExecutionFailedException;
import axoloti.swingui.TextEditor;
import axoloti.swingui.components.PresetPanel;
import axoloti.swingui.components.VisibleCablePanel;
import axoloti.swingui.mvc.UndoListViewFrame;
import axoloti.swingui.mvc.UndoUI;
import axoloti.target.TargetModel;
import axoloti.target.fs.SDCardMountStatusListener;
import axoloti.utils.Constants;
import axoloti.utils.KeyUtils;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author Johannes Taelman
 */
public class PatchFrame extends javax.swing.JFrame implements DocumentWindow, ConnectionStatusListener, SDCardMountStatusListener, IView, IAbstractEditor {

    /**
     * Creates new form PatchFrame
     */
    private final PatchModel patchModel;
    private final PatchController patchController;
    private final PatchView patchView;

    private PatchSettingsFrame patchSettingsEditor;

    private PresetPanel presetPanel;
    private VisibleCablePanel visibleCablePanel;

    private UndoUI undoUi;

    private JScrollPane jScrollPane1;

    public PatchFrame(final PatchModel patchModel) {
        this(patchModel, false);
    }

    public PatchFrame(final PatchModel patchModel, boolean usePiccolo) {
        this.patchModel = patchModel;
        this.patchController = patchModel.getController();
        patchView = PatchViewFactory.patchViewFactory(patchModel);
        initComponents();
        initComponents2();
        if (usePiccolo) {
            initializeZoomMenuItems();
        }
    }

    private void initComponents2() {
        patchView.postConstructor();
        setIconImage(new ImageIcon(getClass().getResource("/resources/axoloti_icon.png")).getImage());

        undoUi = new UndoUI(patchController.getUndoManager());
        if (patchController.getDocumentRoot() != null) {
            patchController.getDocumentRoot().addUndoListener(undoUi);
        }

        JMenuItem menuItemNewSwingView = new JMenuItem("New Swing View");
        menuItemNewSwingView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PatchViewType save = Preferences.getPreferences().getPatchViewType();
                Preferences.getPreferences().setPatchViewType(PatchViewType.SWING);
                PatchFrame pf = new PatchFrame(patchModel);
                patchController.addView(pf);
                pf.setVisible(true);
                Preferences.getPreferences().setPatchViewType(save);
            }
        });
        fileMenu1.add(menuItemNewSwingView);

        JMenuItem menuItemNewPiccoloView = new JMenuItem("New Piccolo View");
        menuItemNewPiccoloView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PatchViewType save = Preferences.getPreferences().getPatchViewType();
                Preferences.getPreferences().setPatchViewType(PatchViewType.PICCOLO);
                PatchFrame pf = new PatchFrame(patchModel, true);
                patchController.addView(pf);
                pf.setVisible(true);
                Preferences.getPreferences().setPatchViewType(save);
            }
        });
        fileMenu1.add(menuItemNewPiccoloView);
        
        JMenuItem menuItemNewUndoListView = new JMenuItem("New UndoList View");
        menuItemNewUndoListView.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    UndoListViewFrame ulvf = new UndoListViewFrame(patchController.getDocumentRoot(), PatchFrame.this);
                    patchController.getDocumentRoot().getController().addView(ulvf);
                    ulvf.setVisible(true);
                }
            });
        fileMenu1.add(menuItemNewUndoListView);

        presetPanel = new PresetPanel(patchView);
        visibleCablePanel = new VisibleCablePanel(getPatchView());

        jToolbarPanel.add(presetPanel);
        jToolbarPanel.add(new javax.swing.Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 32767)));
        jToolbarPanel.add(visibleCablePanel);

        jScrollPane1 = getPatchView().getViewportView().getScrollPane();
        jScrollPane1.setViewportView(getPatchView().getViewportView().getComponent());

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setAutoscrolls(true);
        getContentPane().add(jScrollPane1);

        jMenuEdit.add(undoUi.createMenuItemUndo());
        jMenuEdit.add(undoUi.createMenuItemRedo());

        JMenuItem menuItemCut = new JMenuItem(new DefaultEditorKit.CutAction());
        menuItemCut.setText("Cut");
        menuItemCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                KeyUtils.CONTROL_OR_CMD_MASK));
        jMenuEdit.add(menuItemCut);
        menuItemCut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PatchModel p = getPatchView().getSelectedObjects();
                if (p.getObjectInstances().isEmpty()) {
                    getToolkit().getSystemClipboard().setContents(new StringSelection(""), null);
                    return;
                }
                Serializer serializer = new Persister();
                try {
                    Clipboard clip = getToolkit().getSystemClipboard();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    serializer.write(p, baos);
                    StringSelection s = new StringSelection(baos.toString());
                    clip.setContents(s, (ClipboardOwner) null);
                    patchController.addMetaUndo("cut");
                    for (IAxoObjectInstance o : p.getObjectInstances()) {
                        patchController.delete(o);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        JMenuItem menuItemCopy = new JMenuItem(new DefaultEditorKit.CopyAction());
        menuItemCopy.setText("Copy");
        menuItemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
                KeyUtils.CONTROL_OR_CMD_MASK));
        jMenuEdit.add(menuItemCopy);
        menuItemCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PatchModel p = getPatchView().getSelectedObjects();
                if (p.getObjectInstances().isEmpty()) {
                    getToolkit().getSystemClipboard().setContents(new StringSelection(""), null);
                    return;
                }
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
        JMenuItem menuItemPaste = new JMenuItem(new DefaultEditorKit.PasteAction());
        menuItemPaste.setText("Paste");
        menuItemPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
                KeyUtils.CONTROL_OR_CMD_MASK));
        jMenuEdit.add(menuItemPaste);
        menuItemPaste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clipboard clip = getToolkit().getSystemClipboard();
                try {
                    getPatchView().paste((String) clip.getData(DataFlavor.stringFlavor), null, false);
                } catch (UnsupportedFlavorException ex) {
                    Logger.getLogger(PatchFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(PatchFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        if (getDModel().getWindowPos() != null) {
            setBounds(getDModel().getWindowPos());
        } else {
            Dimension d = getPatchView().getInitialSize();
            setSize(d);
        }

        if (!Preferences.getPreferences().getExpertMode()) {
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

        if (CConnection.getConnection().isConnected()) {
            showConnect();
        }

        CConnection.getConnection().addConnectionStatusListener(this);
        CConnection.getConnection().addSDCardMountStatusListener(this);

        getPatchView().getViewportView().getComponent().requestFocusInWindow();


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                jScrollPane1.setWheelScrollingEnabled(Preferences.getPreferences().getMouseWheelPan());
            }

            @Override
            public void windowClosing(WindowEvent ev) {
                askClose();
            }
        });

        addComponentListener(new ComponentAdapter() {

            void update() {
                // this is not an undoable property
                getDModel().setWindowPos(getBounds());
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                update();
            }

            @Override
            public void componentResized(ComponentEvent e) {
                update();
            }
        });

        patchController.addView(this);
        patchController.addView(patchView);
    }

    private void initializeZoomMenuItems() {
        JMenuItem zoomInMenuItem = new JMenuItem();
        zoomInMenuItem.setText("Zoom in");
        zoomInMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS,
                KeyUtils.CONTROL_OR_CMD_MASK));
        zoomInMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getPatchView().getViewportView().zoomIn();
            }
        });
        JMenuItem zoomOutMenuItem = new JMenuItem();
        zoomOutMenuItem.setText("Zoom out");
        zoomOutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS,
                KeyUtils.CONTROL_OR_CMD_MASK));
        zoomOutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getPatchView().getViewportView().zoomOut();
            }
        });
        JMenuItem zoomDefaultMenuItem = new JMenuItem();
        zoomDefaultMenuItem.setText("Zoom to default");
        zoomDefaultMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0,
                KeyUtils.CONTROL_OR_CMD_MASK));
        zoomDefaultMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getPatchView().getViewportView().zoomDefault();
            }
        });
        jMenuView.add(zoomInMenuItem);
        jMenuView.add(zoomOutMenuItem);
        jMenuView.add(zoomDefaultMenuItem);
    }

    private PatchView getPatchView() {
        return patchView;
    }

    @Override
    public PatchModel getDModel() {
        return patchModel;
    }

    public void scrollTo(Rectangle rect) {
        jScrollPane1.scrollRectToVisible(rect);
    }

    private void setLive(boolean b) {
        if (b) {
            jCheckBoxLive.setSelected(true);
            jCheckBoxLive.setEnabled(true);
            jCheckBoxMenuItemLive.setSelected(true);
            jCheckBoxMenuItemLive.setEnabled(true);
            presetPanel.showLive(true);
        } else {
            jCheckBoxLive.setSelected(false);
            jCheckBoxLive.setEnabled(true);
            jCheckBoxMenuItemLive.setSelected(false);
            jCheckBoxMenuItemLive.setEnabled(true);
            presetPanel.showLive(false);
        }
    }

    void showConnect1(boolean status) {
        jCheckBoxLive.setEnabled(status);
        jCheckBoxMenuItemLive.setEnabled(status);
        jMenuItemUploadInternalFlash.setEnabled(status);
        jMenuItemUploadSD.setEnabled(status);
        jMenuItemUploadSDStart.setEnabled(status);
    }

    @Override
    public void showDisconnect() {
        if (patchController.isLocked()) {
            patchController.setLocked(false);
        }
        jCheckBoxLive.setSelected(false);
        jCheckBoxMenuItemLive.setSelected(false);
        showConnect1(false);
    }

    @Override
    public void showConnect() {
        jCheckBoxLive.setSelected(patchController.isLocked());
        jCheckBoxMenuItemLive.setSelected(patchController.isLocked());
        showConnect1(true);
    }

    public void showCompileFail() {
        jCheckBoxLive.setSelected(false);
        jCheckBoxLive.setEnabled(true);
    }

    @Override
    public void close() {
        DocumentWindowList.unregisterWindow(this);
        CConnection.getConnection().removeConnectionStatusListener(this);
        CConnection.getConnection().removeSDCardMountStatusListener(this);
        getPatchView().dispose();
        dispose();
    }

    @Override
    public boolean askClose() {
        if (!patchController.getDocumentRoot().getDirty()) {
            close();
            return false;
        }
        if (getDModel().getParent() == null) {
            Object[] options = {"Save",
                "Don't save",
                "Cancel"};
            int n = JOptionPane.showOptionDialog(this,
                    "Do you want to save changes to " + getDModel().getFileNamePath() + " ?",
                    "Axoloti asks:",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[2]);
            switch (n) {
                case JOptionPane.YES_OPTION:
                    jMenuSaveActionPerformed(null);
                    close();
                    return false;
                case JOptionPane.NO_OPTION:
                    close();
                    return false;
                case JOptionPane.CANCEL_OPTION:
                    return true;
                default:
                    return false;
            }
        } else {
            close();
            return false;
        }
    }

    private TextEditor notesEditor;

    void showNotesFrame() {
        if (notesEditor == null) {
            notesEditor = new TextEditor(PatchModel.PATCH_NOTES, patchModel, this);
            patchController.addView(notesEditor);
            notesEditor.setTitle("notes");
        }
        notesEditor.setVisible(true);
        notesEditor.toFront();
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
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu1 = new axoloti.swingui.menus.FileMenu();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuSave = new javax.swing.JMenuItem();
        jMenuSaveAs = new javax.swing.JMenuItem();
        jMenuSaveCopy = new javax.swing.JMenuItem();
        jMenuSaveClip = new javax.swing.JMenuItem();
        jMenuClose = new javax.swing.JMenuItem();
        jMenuEdit = new javax.swing.JMenu();
        jMenuItemDelete = new javax.swing.JMenuItem();
        jMenuItemSelectAll = new javax.swing.JMenuItem();
        jMenuItemAddObj = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuView = new javax.swing.JMenu();
        jMenuItemNotes = new javax.swing.JMenuItem();
        jMenuItemSettings = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jCheckBoxMenuItemCordsInBackground = new javax.swing.JCheckBoxMenuItem();
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
        targetMenu = new axoloti.swingui.target.TargetMenu(axoloti.target.TargetModel.getTargetModel());
        windowMenu1 = new axoloti.swingui.menus.WindowMenu();
        helpMenu1 = new axoloti.swingui.menus.HelpMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
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

        fileMenu1.initComponents();
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

        targetMenu.setText("Board");
        jMenuBar1.add(targetMenu);
        jMenuBar1.add(windowMenu1);

        helpMenu1.setText("Help");
        jMenuBar1.add(helpMenu1);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBoxLiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxLiveActionPerformed
        if (jCheckBoxLive.isSelected()) {
            if (goLive()) {
                // success
            } else {
                jCheckBoxLive.setSelected(false);
            }
        } else {
            patchController.setLocked(false);
            try {
                CConnection.getConnection().transmitStop();
            } catch (IOException ex) {
                Logger.getLogger(PatchFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jCheckBoxLiveActionPerformed

    private void jMenuSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuSaveActionPerformed
        String fn = getDModel().getFileNamePath();
        if ((fn != null) && (!fn.equals("untitled"))) {
            File f = new File(fn);
            patchController.setFileNamePath(f.getPath());
            getPatchView().save(f);
        } else {
            jMenuSaveAsActionPerformed(evt);
        }
    }//GEN-LAST:event_jMenuSaveActionPerformed

    File askFileToSave() {
        final JFileChooser fc = new JFileChooser(Preferences.getPreferences().getCurrentFileDirectory());
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(FileUtils.axpFileFilter);
        fc.addChoosableFileFilter(FileUtils.axsFileFilter);
        fc.addChoosableFileFilter(FileUtils.axhFileFilter);
        String fn = getDModel().getFileNamePath();
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
        File fileToBeSaved = askFileToSave();
        if (fileToBeSaved != null) {
            patchController.setFileNamePath(fileToBeSaved.getPath());
            Preferences.getPreferences().setCurrentFileDirectory(fileToBeSaved.getPath());
            getPatchView().save(fileToBeSaved);
        }
    }//GEN-LAST:event_jMenuSaveAsActionPerformed

    private void jCheckBoxMenuItemCordsInBackgroundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemCordsInBackgroundActionPerformed
        getPatchView().setCordsInBackground(jCheckBoxMenuItemCordsInBackground.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItemCordsInBackgroundActionPerformed

    private void jMenuGenerateCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuGenerateCodeActionPerformed
        patchController.writeCode();
    }//GEN-LAST:event_jMenuGenerateCodeActionPerformed

    private void jMenuCompileCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuCompileCodeActionPerformed
        try {
            patchController.compile();
        } catch (ExecutionFailedException ex) {
            Logger.getLogger(PatchFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuCompileCodeActionPerformed

    private void jMenuUploadCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuUploadCodeActionPerformed
        try {
            //patchController.GetQCmdProcessor().setPatchController(null);
            IConnection conn = CConnection.getConnection();
            conn.transmitStop();
            TargetModel.getTargetModel().uploadPatchToMemory();
//        patchController.GetQCmdProcessor().AppendToQueue(new QCmdStart(patchController));
//patchController.GetQCmdProcessor().AppendToQueue(new QCmdLock(patchController));
        } catch (IOException ex) {
            Logger.getLogger(PatchFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuUploadCodeActionPerformed

    private void jMenuItemLockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemLockActionPerformed
        //getPatchView().Lock();
    }//GEN-LAST:event_jMenuItemLockActionPerformed

    private void jMenuItemUnlockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUnlockActionPerformed
        //getPatchView().Unlock();
    }//GEN-LAST:event_jMenuItemUnlockActionPerformed

    private void jMenuItemClearPresetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemClearPresetActionPerformed
        getDModel().clearCurrentPreset();
    }//GEN-LAST:event_jMenuItemClearPresetActionPerformed

    private void jMenuItemPresetCurrentToInitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPresetCurrentToInitActionPerformed
        getDModel().copyCurrentToInit();
    }//GEN-LAST:event_jMenuItemPresetCurrentToInitActionPerformed

    private void jMenuItemDifferenceToPresetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDifferenceToPresetActionPerformed
        getDModel().differenceToPreset();
    }//GEN-LAST:event_jMenuItemDifferenceToPresetActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItemNotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNotesActionPerformed
        showNotesFrame();
    }//GEN-LAST:event_jMenuItemNotesActionPerformed

    private void jMenuItemSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSettingsActionPerformed

        if (patchSettingsEditor == null) {
            patchSettingsEditor = new PatchSettingsFrame(this, patchModel);
            patchController.addView(patchSettingsEditor);
        }
        patchSettingsEditor.toFront();

        /*
        // Needs review: why should edit->settings give to access to the object editor???
        IAxoObjectInstanceView selObj = null;
        for (IAxoObjectInstanceView i : getPatchView().getObjectInstanceViews()) {
            if (i.getModel().getSelected() && i instanceof AxoObjectInstanceView) {
                selObj = i;
            }
        }

        if (selObj != null) {
            ((AxoObjectInstanceView) selObj).OpenEditor();
        } else {
            PatchSettingsFrame psf = new PatchSettingsFrame(getController());
            getController().addView(psf);
            psf.setVisible(true);
        }
         */
    }//GEN-LAST:event_jMenuItemSettingsActionPerformed

    private void jCheckBoxMenuItemLiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemLiveActionPerformed
        if (jCheckBoxMenuItemLive.isSelected()) {
            if (goLive()) {
                jCheckBoxMenuItemLive.setEnabled(false);
            } else {
                jCheckBoxMenuItemLive.setSelected(false);
            }
        } else {
            try {
                patchController.setLocked(false);
                IConnection conn = CConnection.getConnection();
                conn.transmitStop();
            } catch (IOException ex) {
                Logger.getLogger(PatchFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jCheckBoxMenuItemLiveActionPerformed

    private void jMenuItemUploadSDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUploadSDActionPerformed
        GlobalJobProcessor.getJobProcessor().exec((ctx) -> {
            try {
                patchController.uploadToSDCard(ctx);
            } catch (IOException|ExecutionFailedException ex) {
                ctx.reportException(ex);
            }
        });
    }//GEN-LAST:event_jMenuItemUploadSDActionPerformed

    private void jMenuItemUploadSDStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUploadSDStartActionPerformed
        GlobalJobProcessor.getJobProcessor().exec((ctx) -> {
            try {
                patchController.uploadToSDCard("/start.bin", ctx);
            } catch (IOException|ExecutionFailedException ex) {
                ctx.reportException(ex);
            }
        });
    }//GEN-LAST:event_jMenuItemUploadSDStartActionPerformed

    private void jMenuSaveClipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuSaveClipActionPerformed
        Serializer serializer = new Persister();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(256 * 1024);
        try {
            serializer.write(getDModel(), baos);
        } catch (Exception ex) {
            Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
        }
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        c.setContents(new StringSelection(baos.toString()), null);
    }//GEN-LAST:event_jMenuSaveClipActionPerformed

    private void jMenuItemUploadInternalFlashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUploadInternalFlashActionPerformed
        patchController.uploadToFlash();
    }//GEN-LAST:event_jMenuItemUploadInternalFlashActionPerformed

    private void jMenuCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuCloseActionPerformed
        askClose();
    }//GEN-LAST:event_jMenuCloseActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        DocumentWindowList.registerWindow(this);
    }//GEN-LAST:event_formComponentShown

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
        DocumentWindowList.unregisterWindow(this);
    }//GEN-LAST:event_formComponentHidden

    private void jMenuSaveCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuSaveCopyActionPerformed
        File fileToBeSaved = askFileToSave();
        if (fileToBeSaved != null) {
            Preferences.getPreferences().setCurrentFileDirectory(fileToBeSaved.getPath());
            getPatchView().save(fileToBeSaved);
        }
    }//GEN-LAST:event_jMenuSaveCopyActionPerformed

    private void jMenuGenerateAndCompileCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuGenerateAndCompileCodeActionPerformed
        try {
            patchController.writeCode();
            patchController.compile();
        } catch (ExecutionFailedException ex) {
            Logger.getLogger(PatchFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuGenerateAndCompileCodeActionPerformed

    private void formWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowLostFocus
        getRootPane().setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_formWindowLostFocus

    private void jMenuItemAddObjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAddObjActionPerformed
        // TODO: add object to a visible location in patch (when scrollbar is active...)
        Point p = new Point(Constants.X_GRID, Constants.Y_GRID);
        while(getDModel().findObjectInstance(p) != null) {
            p.x += Constants.X_GRID;
            p.y += Constants.Y_GRID;
        }
        getPatchView().showClassSelector(p, null, null, null);
    }//GEN-LAST:event_jMenuItemAddObjActionPerformed

    private void jMenuItemSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSelectAllActionPerformed
        patchController.selectAll();
    }//GEN-LAST:event_jMenuItemSelectAllActionPerformed

    private void jMenuItemDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDeleteActionPerformed
        List<IAxoObjectInstance> selected = getDModel().getSelectedObjects();
        if (!selected.isEmpty()) {
            patchController.addMetaUndo("delete objects");
            for (IAxoObjectInstance o : selected) {
                patchController.delete(o);
            }
        }
    }//GEN-LAST:event_jMenuItemDeleteActionPerformed

    private boolean goLive() {
        if (getDModel().getFileNamePath().endsWith(".axs")
                || (patchModel.getParent() != null)) {
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
        PatchViewCodegen pvcg = patchModel.getController().writeCode();
        PatchViewLive pvl = new PatchViewLive(patchModel, pvcg);
        pvl.goLive();
        return true;
    }

    /* write to sdcard...
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private axoloti.swingui.menus.FileMenu fileMenu1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private axoloti.swingui.menus.HelpMenu helpMenu1;
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
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPanel jToolbarPanel;
    private javax.swing.JMenu targetMenu;
    private axoloti.swingui.menus.WindowMenu windowMenu1;
    // End of variables declaration//GEN-END:variables

    void showDSPLoad(int pct) {
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
    public File getFile() {
        if (getDModel().getFileNamePath() == null) {
            return null;
        } else {
            return new File(getDModel().getFileNamePath());
        }
    }

    private ArrayList<DocumentWindow> dwl = new ArrayList<>();

    @Override
    public List<DocumentWindow> getChildDocuments() {
        return dwl;
    }

    @Override
    public void showSDCardMounted() {
        jMenuItemUploadSD.setEnabled(true);
        jMenuItemUploadSDStart.setEnabled(true);
    }

    @Override
    public void showSDCardUnmounted() {
        jMenuItemUploadSD.setEnabled(false);
        jMenuItemUploadSDStart.setEnabled(false);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (PatchModel.PATCH_LOCKED.is(evt)) {
            if ((Boolean)evt.getNewValue() == false) {
                setLive(false);
            } else {
                setLive(true);
            }
        } else if (PatchModel.PATCH_DSPLOAD.is(evt)) {
            showDSPLoad((Integer) evt.getNewValue());
        } else if (PatchModel.PATCH_FILENAME.is(evt)) {
            this.setTitle((String)evt.getNewValue());
        } else if (PatchModel.PATCH_WINDOWPOS.is(evt)) {
            // do NOT respond to this event
            // multiple views would track position/size
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (patchSettingsEditor != null) {
            patchSettingsEditor.dispose();
        }
        if (notesEditor != null) {
            notesEditor.dispose();
        }
    }

    @Override
    public void toFront() {
        setState(java.awt.Frame.NORMAL);
        super.toFront();
    }


}
