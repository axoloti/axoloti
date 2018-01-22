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
package axoloti.swingui.patchbank;

import axoloti.ConnectionStatusListener;
import static axoloti.FileUtils.axpFileFilter;
import axoloti.abstractui.DocumentWindow;
import axoloti.abstractui.DocumentWindowList;
import axoloti.connection.CConnection;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.patchbank.PatchBankController;
import axoloti.patchbank.PatchBankModel;
import axoloti.preferences.Preferences;
import axoloti.swingui.menus.StandardMenubar;
import axoloti.swingui.mvc.AJFrame;
import axoloti.swingui.patch.PatchViewSwing;
import axoloti.target.TargetModel;
import axoloti.target.fs.SDCardMountStatusListener;
import axoloti.target.fs.SDFileInfo;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author jtaelman
 */
public class PatchBank extends AJFrame<PatchBankController> implements DocumentWindow, ConnectionStatusListener, SDCardMountStatusListener {

    /**
     * Creates new form PatchBank
     */
    public PatchBank(PatchBankController controller) {
        super(controller, null);
        initComponents();
        StandardMenubar menuBar = new StandardMenubar(controller.getDocumentRoot());
        JMenuItem jMenuItemSave = new JMenuItem("Save");
        jMenuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveActionPerformed(evt);
            }
        });
        menuBar.fileMenu.add(jMenuItemSave);

        JMenuItem jMenuItemSaveAs = new JMenuItem("Save as...");
        jMenuItemSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveAsActionPerformed(evt);
            }
        });
        menuBar.fileMenu.add(jMenuItemSaveAs);
        setJMenuBar(menuBar);

        CConnection.GetConnection().addConnectionStatusListener(this);
        jTable1.setModel(new AbstractTableModel() {
            private final String[] columnNames = {"Index", "File", "on sdcard"};

            @Override
            public int getColumnCount() {
                return columnNames.length;
            }

            @Override
            public String getColumnName(int column) {
                return columnNames[column];
            }

            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    case 1:
                        return String.class;
                    case 2:
                        return String.class;
                }
                return null;
            }

            @Override
            public int getRowCount() {
                return getModel().getFiles().size();
            }

            @Override
            public void setValueAt(Object value, int rowIndex, int columnIndex) {

                switch (columnIndex) {
                    case 0:
                        break;
                    case 1:
                        String svalue = (String) value;
                        if (svalue != null && !svalue.isEmpty()) {
                            File f = new File(svalue);
                            if (f.exists() && f.isFile() && f.canRead()) {
                                ArrayList<File> files = new ArrayList<>(getModel().getFiles());
                                files.set(rowIndex, f);
                                getController().addMetaUndo("Change");
                                getController().setModelUndoableProperty(PatchBankModel.FILES, files);
                            }
                        }
                        break;
                    case 2:
                        break;
                }
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                Object returnValue = null;

                switch (columnIndex) {
                    case 0:
                        returnValue = Integer.toString(rowIndex);
                        break;
                    case 1: {
                        File f = getModel().getFiles().get(rowIndex);
                        if (f != null) {
                            returnValue = getController().getModel().toRelative(f);
                        } else {
                            returnValue = "";
                        }
                        break;
                    }
                    case 2: {
                        File f = getModel().getFiles().get(rowIndex);
                        if (f != null) {
                            boolean en = f.exists();
                            String fn = f.getName();
                            int i = fn.lastIndexOf('.');
                            if (i > 0) {
                                fn = fn.substring(0, i);
                            }
                            SDFileInfo sdfi = TargetModel.getTargetModel().getSDCardInfo().find("/" + fn + "/patch.bin");
                            if (sdfi != null) {
                                if (en) {
                                    returnValue = "resolved locally, and exists on sdcard";
                                } else {
                                    returnValue = "UNresolved locally, but exists on sdcard";
                                }
                            } else if (en) {
                                returnValue = "resolved locally, not on sdcard";
                            } else {
                                returnValue = "UNresolved locally, not on sdcard";
                            }
                        }
                        break;
                    }
                }

                return returnValue;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return (columnIndex == 1);
            }

        });

        jTable1.getColumnModel().getColumn(0).setPreferredWidth(10);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(90);
        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                reflectSelection(jTable1.getSelectedRow());
            }
        });
        reflectSelection(-1);
    }

    final void reflectSelection(int row) {
        if (row < 0) {
            jButtonUp.setEnabled(false);
            jButtonDown.setEnabled(false);
            jButtonOpen.setEnabled(false);
            jButtonRemove.setEnabled(false);
        } else {
            jButtonUp.setEnabled(row > 0);
            jButtonDown.setEnabled(row < getModel().getFiles().size() - 1);
            File f = getModel().getFiles().get(row);
            boolean en = (f != null) && (f.exists());
            jButtonOpen.setEnabled(en);
            jButtonUpload.setEnabled(en);
            jButtonRemove.setEnabled(true);
        }
    }

    PatchBankModel getModel() {
        return getController().getModel();
    }

    void SaveAs() {
        final JFileChooser fc = new JFileChooser(Preferences.getPreferences().getCurrentFileDirectory());
        fc.setAcceptAllFileFilterUsed(false);
        FileFilter axb = new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.getName().endsWith("axb")) {
                    return true;
                } else if (file.isDirectory()) {
                    return true;
                }
                return false;
            }

            @Override
            public String getDescription() {
                return "Axoloti Patch Bank";
            }
        };
        fc.addChoosableFileFilter(axb);
        File f = getModel().getFile();
        if ((f == null) || (!f.exists())) {
            f = new File(Preferences.getPreferences().getCurrentFileDirectory());
        }
        fc.setSelectedFile(f);
        fc.setFileFilter(axb);
        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String filterext = PatchBankModel.fileExtension;
            if (fc.getFileFilter() == axb) {
                filterext = PatchBankModel.fileExtension;
            }

            File fileToBeSaved = fc.getSelectedFile();
            String ext = PatchBankModel.fileExtension;
            String fname = fileToBeSaved.getAbsolutePath();
            if (!ext.equalsIgnoreCase(PatchBankModel.fileExtension)) {
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
                        return;
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
                        return;
                }
            }
            getModel().setFile(fileToBeSaved);
            getModel().Save();
            getController().getDocumentRoot().markSaved();
            Preferences.getPreferences().setCurrentFileDirectory(fileToBeSaved.getPath());
        }
    }

    public void Close() {
        DocumentWindowList.UnregisterWindow(this);
        CConnection.GetConnection().removeConnectionStatusListener(this);
        dispose();
    }

    @Override
    public boolean askClose() {
        if (getController().getDocumentRoot().getDirty()) {
            Object[] options = {"Save",
                "Don't save",
                "Cancel"};
            String filename = "untitled";
            File f = getController().getModel().getFile();
            if (f != null) {
                filename = f.getName();
            }
            int n = JOptionPane.showOptionDialog(this,
                    "Do you want to save changes to " + filename + " ?",
                    "Axoloti asks:",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[2]);
            switch (n) {
                case JOptionPane.YES_OPTION:
                    SaveAs();
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButtonUploadBank = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jUploadAll = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jButtonUp = new javax.swing.JButton();
        jButtonDown = new javax.swing.JButton();
        jButtonRemove = new javax.swing.JButton();
        jButtonAdd = new javax.swing.JButton();
        jButtonOpen = new javax.swing.JButton();
        jButtonUpload = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Untitled patch bank");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jButtonUploadBank.setText("Upload bank table");
        jButtonUploadBank.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUploadBankActionPerformed(evt);
            }
        });

        jLabel1.setText("Not (fully) implemented yet!");

        jUploadAll.setText("Upload Patch Bank");
        jUploadAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jUploadAllActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonUploadBank)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 108, Short.MAX_VALUE)
                .addComponent(jUploadAll)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonUploadBank)
                    .addComponent(jUploadAll))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "File"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        getContentPane().add(jScrollPane1);

        jButtonUp.setText("Move up");
        jButtonUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpActionPerformed(evt);
            }
        });

        jButtonDown.setText("Move Down");
        jButtonDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDownActionPerformed(evt);
            }
        });

        jButtonRemove.setText("Remove");
        jButtonRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveActionPerformed(evt);
            }
        });

        jButtonAdd.setText("Add");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });

        jButtonOpen.setText("Open");
        jButtonOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOpenActionPerformed(evt);
            }
        });

        jButtonUpload.setText("Upload");
        jButtonUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUploadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonUp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonDown)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonRemove)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonAdd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonOpen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addComponent(jButtonUpload)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonUp)
                    .addComponent(jButtonDown)
                    .addComponent(jButtonRemove)
                    .addComponent(jButtonAdd)
                    .addComponent(jButtonOpen)
                    .addComponent(jButtonUpload)))
        );

        getContentPane().add(jPanel2);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonUploadBankActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUploadBankActionPerformed
        getController().getModel().upload();
    }//GEN-LAST:event_jButtonUploadBankActionPerformed

    private void jMenuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {
        if (getFile() == null) {
            SaveAs();
        } else {
            if (!getFile().canWrite()) {
                SaveAs();
            } else {
                getModel().Save();
                getController().getDocumentRoot().markSaved();
            }
        }
    }

    private void jMenuItemSaveAsActionPerformed(java.awt.event.ActionEvent evt) {
        SaveAs();
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        askClose();
    }//GEN-LAST:event_formWindowClosing

    private void jButtonUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpActionPerformed
        int row = jTable1.getSelectedRow();
        if (row < 1) {
            return;
        }
        ArrayList<File> files = new ArrayList<>(getModel().getFiles());
        File o = files.remove(row);
        files.add(row - 1, o);
        getController().addMetaUndo("Move up");
        getController().setModelUndoableProperty(PatchBankModel.FILES, files);
        jTable1.setRowSelectionInterval(row - 1, row - 1);
    }//GEN-LAST:event_jButtonUpActionPerformed

    private void jButtonDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDownActionPerformed
        int row = jTable1.getSelectedRow();
        if (row < 0) {
            return;
        }
        ArrayList<File> files = new ArrayList<>(getModel().getFiles());
        if (row > (files.size() - 1)) {
            return;
        }
        File o = files.remove(row);
        files.add(row + 1, o);
        getController().addMetaUndo("Move down");
        getController().setModelUndoableProperty(PatchBankModel.FILES, files);
        jTable1.setRowSelectionInterval(row + 1, row + 1);
    }//GEN-LAST:event_jButtonDownActionPerformed

    private void jButtonRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveActionPerformed
        int row = jTable1.getSelectedRow();
        if (row < 0) {
            return;
        }
        ArrayList<File> files = new ArrayList<>(getModel().getFiles());
        files.remove(row);
        getController().addMetaUndo("Remove");
        getController().setModelUndoableProperty(PatchBankModel.FILES, files);
    }//GEN-LAST:event_jButtonRemoveActionPerformed

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        JFileChooser fc = new JFileChooser(Preferences.getPreferences().getCurrentFileDirectory());
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(axpFileFilter);
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            ArrayList<File> files = new ArrayList<>(getModel().getFiles());
            files.add(fc.getSelectedFile());
            getController().addMetaUndo("Add");
            getController().setModelUndoableProperty(PatchBankModel.FILES, files);
        }
    }//GEN-LAST:event_jButtonAddActionPerformed

    private void jButtonOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOpenActionPerformed
        int row = jTable1.getSelectedRow();
        if (row >= 0) {
            File f = getModel().getFiles().get(jTable1.getSelectedRow());
            if (f.isFile() && f.canRead()) {
                PatchViewSwing.OpenPatch(f);
            }
        }
    }//GEN-LAST:event_jButtonOpenActionPerformed

    private void jButtonUploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUploadActionPerformed
        File f = getModel().getFiles().get(jTable1.getSelectedRow());
        getController().getModel().UploadOneFile(f);
    }//GEN-LAST:event_jButtonUploadActionPerformed

    private void jUploadAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jUploadAllActionPerformed
        getController().getModel().uploadAll();
    }//GEN-LAST:event_jUploadAllActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonDown;
    private javax.swing.JButton jButtonOpen;
    private javax.swing.JButton jButtonRemove;
    private javax.swing.JButton jButtonUp;
    private javax.swing.JButton jButtonUpload;
    private javax.swing.JButton jButtonUploadBank;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jUploadAll;
    // End of variables declaration//GEN-END:variables

    public void ShowConnect1(boolean status) {
        jButtonUploadBank.setEnabled(status);
        jButtonUpload.setEnabled(status);
        jUploadAll.setEnabled(status);
    }

    @Override
    public void ShowConnect() {
        ShowConnect1(true);
    }

    @Override
    public void ShowDisconnect() {
        ShowConnect1(false);
    }

    @Override
    public File getFile() {
        return getController().getModel().getFile();
    }

    @Override
    public ArrayList<DocumentWindow> getChildDocuments() {
        return null;
    }

    @Override
    public void ShowSDCardMounted() {
        ShowConnect1(true);
    }

    @Override
    public void ShowSDCardUnmounted() {
        ShowConnect1(false);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (PatchBankModel.FILES.is(evt)) {
            ((AbstractTableModel) jTable1.getModel()).fireTableDataChanged();
        } else if (PatchBankModel.FILE.is(evt)) {
            File f = getFile();
            if (f != null) {
                setTitle(f.getName());
            }
        }
    }

    public static void OpenPatchBankEditor(InputStream inputStream, String filename) {
        try {
            PatchBankModel b;
            if (inputStream == null) {
                b = new PatchBankModel();
            } else {
                b = new PatchBankModel(inputStream, filename);
            }
            AbstractDocumentRoot documentRoot = new AbstractDocumentRoot();
            PatchBankController c = new PatchBankController(b, documentRoot, null);
            PatchBank bv = new PatchBank(c);
            documentRoot.getUndoManager().discardAllEdits();
            c.addView(bv);
            bv.setVisible(true);
            bv.toFront();
        } catch (IOException ex) {
            Logger.getLogger(PatchBank.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void OpenPatchBankEditor(File f) {
        try {
            PatchBankModel b;
            if (f == null) {
                OpenPatchBankEditor(null, "Untitled.axb");
            } else {
                FileInputStream fstream = new FileInputStream(f);
                OpenPatchBankEditor(fstream, f.getAbsolutePath());
            }
        } catch (IOException ex) {
            Logger.getLogger(PatchBank.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
