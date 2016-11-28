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
package axoloti.dialogs;

import axoloti.ConnectionStatusListener;
import axoloti.DocumentWindow;
import axoloti.DocumentWindowList;
import static axoloti.FileUtils.axpFileFilter;
import axoloti.MainFrame;
import static axoloti.MainFrame.prefs;
import axoloti.PatchFrame;
import axoloti.PatchGUI;
import axoloti.SDCardInfo;
import axoloti.SDCardMountStatusListener;
import axoloti.SDFileInfo;
import axoloti.USBBulkConnection;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import qcmds.QCmdProcessor;
import qcmds.QCmdUploadFile;

/**
 *
 * @author jtaelman
 */
public class PatchBank extends javax.swing.JFrame implements DocumentWindow, ConnectionStatusListener, SDCardMountStatusListener {

    String FilenamePath = null;

    final String fileExtension = ".axb";

    boolean dirty = false;

    ArrayList<File> files;

    /**
     * Creates new form PatchBank
     */
    public PatchBank() {
        initComponents();
        fileMenu1.initComponents();
        files = new ArrayList<File>();
        setIconImage(new ImageIcon(getClass().getResource("/resources/axoloti_icon.png")).getImage());
        DocumentWindowList.RegisterWindow(this);
        USBBulkConnection.GetConnection().addConnectionStatusListener(this);
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
                return files.size();
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
                                files.set(rowIndex, f);
                                setDirty();
                                refresh();
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
                        File f = files.get(rowIndex);
                        if (f != null) {
                            returnValue = toRelative(f);
                        } else {
                            returnValue = "";
                        }
                        break;
                    }
                    case 2: {
                        File f = files.get(rowIndex);
                        if (f != null) {
                            boolean en = f.exists();
                            String fn = f.getName();
                            int i = fn.lastIndexOf('.');
                            if (i > 0) {
                                fn = fn.substring(0, i);
                            }
                            SDFileInfo sdfi = SDCardInfo.getInstance().find("/" + fn + "/patch.bin");
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
            jButtonDown.setEnabled(row < files.size() - 1);
            File f = files.get(row);
            boolean en = (f != null) && (f.exists());
            jButtonOpen.setEnabled(en);
            jButtonUpload.setEnabled(en);
            jButtonRemove.setEnabled(true);
        }
    }

    public void refresh() {
        jTable1.revalidate();
        jTable1.repaint();
    }

    String toRelative(File f) {
        if (FilenamePath != null && !FilenamePath.isEmpty()) {
            Path path = Paths.get(f.getPath());
            Path pathBase = Paths.get(new File(FilenamePath).getParent());
            if (path.isAbsolute()) {
                Path pathRelative = pathBase.relativize(path);
                return pathRelative.toString();
            } else {
                return path.toString();
            }
        } else {
            return f.getAbsolutePath();
        }
    }

    File fromRelative(String s) {
        Path basePath = FileSystems.getDefault().getPath(FilenamePath);
        Path resolvedPath = basePath.getParent().resolve(s);
        return resolvedPath.toFile();
    }

    public byte[] GetContents() {
        ByteBuffer data = ByteBuffer.allocateDirect(128 * 256);
        for (File file : files) {
            String fn = (String) file.getName();
            for (char c : fn.toCharArray()) {
                data.put((byte) c);
            }
            data.put((byte) '\n');
        }
        data.limit(data.position());
        data.rewind();
        byte[] b = new byte[data.limit()];
        data.get(b);
        return b;
    }

    void Open(File f) throws IOException {
        FilenamePath = f.getPath();
        InputStream fs = new FileInputStream(f);
        BufferedReader fbs = new BufferedReader(new InputStreamReader(fs));
        String s;
        files = new ArrayList<File>();
        while ((s = fbs.readLine())
                != null) {
            File ff = fromRelative(s);
            if (ff != null) {
                files.add(ff);
            }
        }
        fs.close();
        refresh();
        setTitle(FilenamePath);
    }

    void Save(File f) {
        FilenamePath = f.getPath();
        try {
            PrintWriter pw = new PrintWriter(f);
            for (File file : files) {
                String fn = toRelative(file);
                pw.println(fn);
            }
            pw.close();
            clearDirty();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PatchBank.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PatchBank.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    void Save() {
        if (FilenamePath == null) {
            SaveAs();
        } else {
            File f = new File(FilenamePath);
            if (!f.canWrite()) {
                SaveAs();
            } else {
                Save(f);
            }
        }
        refresh();
    }

    void SaveAs() {
        final JFileChooser fc = new JFileChooser(MainFrame.prefs.getCurrentFileDirectory());
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
        String fn = FilenamePath;
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
        if (ext.equalsIgnoreCase(fileExtension)) {
            fc.setFileFilter(axb);
        }

        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String filterext = fileExtension;
            if (fc.getFileFilter() == axb) {
                filterext = fileExtension;
            }

            File fileToBeSaved = fc.getSelectedFile();
            ext = "";
            String fname = fileToBeSaved.getAbsolutePath();
            dot = fname.lastIndexOf('.');
            if (dot > 0 && fname.length() > dot + 3) {
                ext = fname.substring(dot);
            }

            if (!ext.equalsIgnoreCase(fileExtension)) {
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

            FilenamePath = fileToBeSaved.getPath();
            setTitle(FilenamePath);
            MainFrame.prefs.setCurrentFileDirectory(fileToBeSaved.getPath());
            Save(fileToBeSaved);
        }
    }

    boolean isDirty() {
        return dirty;
    }

    void setDirty() {
        dirty = true;
    }

    void clearDirty() {
        dirty = false;
    }

    public void Close() {
        DocumentWindowList.UnregisterWindow(this);
        USBBulkConnection.GetConnection().removeConnectionStatusListener(this);
        dispose();
    }

    @Override
    public boolean AskClose() {
        if (isDirty()) {
            Object[] options = {"Save",
                "Don't save",
                "Cancel"};
            int n = JOptionPane.showOptionDialog(this,
                    "Do you want to save changes to " + FilenamePath + " ?",
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
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu1 = new axoloti.menus.FileMenu();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItemSave = new javax.swing.JMenuItem();
        jMenuItemSaveAs = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        windowMenu1 = new axoloti.menus.WindowMenu();

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

        fileMenu1.setText("File");
        fileMenu1.add(jSeparator1);

        jMenuItemSave.setText("Save");
        jMenuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveActionPerformed(evt);
            }
        });
        fileMenu1.add(jMenuItemSave);

        jMenuItemSaveAs.setText("Save as...");
        jMenuItemSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveAsActionPerformed(evt);
            }
        });
        fileMenu1.add(jMenuItemSaveAs);

        jMenuBar1.add(fileMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);
        jMenuBar1.add(windowMenu1);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonUploadBankActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUploadBankActionPerformed
        QCmdProcessor processor = MainFrame.mainframe.getQcmdprocessor();
        if (USBBulkConnection.GetConnection().isConnected()) {
            processor.AppendToQueue(new QCmdUploadFile(new ByteArrayInputStream(GetContents()), "/index.axb"));
        }
    }//GEN-LAST:event_jButtonUploadBankActionPerformed

    private void jMenuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveActionPerformed
        Save();
    }//GEN-LAST:event_jMenuItemSaveActionPerformed

    private void jMenuItemSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsActionPerformed
        SaveAs();
    }//GEN-LAST:event_jMenuItemSaveAsActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        AskClose();
    }//GEN-LAST:event_formWindowClosing

    private void jButtonUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpActionPerformed
        int row = jTable1.getSelectedRow();
        if (row < 1) {
            return;
        }
        File o = files.remove(row);
        files.add(row - 1, o);
        jTable1.setRowSelectionInterval(row - 1, row - 1);
        setDirty();
        refresh();
    }//GEN-LAST:event_jButtonUpActionPerformed

    private void jButtonDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDownActionPerformed
        int row = jTable1.getSelectedRow();
        if (row < 0) {
            return;
        }
        if (row > (files.size() - 1)) {
            return;
        }
        File o = files.remove(row);
        files.add(row + 1, o);
        jTable1.setRowSelectionInterval(row + 1, row + 1);
        setDirty();
        refresh();
    }//GEN-LAST:event_jButtonDownActionPerformed

    private void jButtonRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveActionPerformed
        int row = jTable1.getSelectedRow();
        if (row < 0) {
            return;
        }
        files.remove(row);
        setDirty();
        refresh();
    }//GEN-LAST:event_jButtonRemoveActionPerformed

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        JFileChooser fc = new JFileChooser(prefs.getCurrentFileDirectory());
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Axoloti Files", "axp"));
        fc.addChoosableFileFilter(axpFileFilter);
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            files.add(fc.getSelectedFile());
            setDirty();
            refresh();
        }
    }//GEN-LAST:event_jButtonAddActionPerformed

    private void jButtonOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOpenActionPerformed
        int row = jTable1.getSelectedRow();
        if (row >= 0) {
            File f = files.get(jTable1.getSelectedRow());
            if (f.isFile() && f.canRead()) {
                PatchGUI.OpenPatch(f);
            }
        }
    }//GEN-LAST:event_jButtonOpenActionPerformed

    void UploadOneFile(File f) {
        if (!f.isFile() || !f.canRead()) {
            return;
        }
        PatchFrame pf = PatchGUI.OpenPatchInvisible(f);
        if (pf != null) {
            boolean isVisible = pf.isVisible();
            PatchGUI p = pf.getPatch();
            p.UploadToSDCard();
            if (!isVisible) {
                pf.Close();
            }

            //FIXME: workaround waitQueueFinished bug
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                ;
            }

            QCmdProcessor.getQCmdProcessor().WaitQueueFinished();
        }
    }


    private void jButtonUploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUploadActionPerformed
        File f = files.get(jTable1.getSelectedRow());
        UploadOneFile(f);
    }//GEN-LAST:event_jButtonUploadActionPerformed

    private void jUploadAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jUploadAllActionPerformed
        Logger.getLogger(MainFrame.class.getName()).log(Level.INFO, "Uploading patch bank file");
        QCmdProcessor processor = MainFrame.mainframe.getQcmdprocessor();
        if (USBBulkConnection.GetConnection().isConnected()) {
            processor.AppendToQueue(new QCmdUploadFile(new ByteArrayInputStream(GetContents()), "/index.axb"));
        }

        for (File f : files) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.INFO, "Compiling and uploading : {0}", f.getName());
            UploadOneFile(f);
        }
        Logger.getLogger(MainFrame.class.getName()).log(Level.INFO, "Patch bank uploaded");
    }//GEN-LAST:event_jUploadAllActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private axoloti.menus.FileMenu fileMenu1;
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonDown;
    private javax.swing.JButton jButtonOpen;
    private javax.swing.JButton jButtonRemove;
    private javax.swing.JButton jButtonUp;
    private javax.swing.JButton jButtonUpload;
    private javax.swing.JButton jButtonUploadBank;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItemSave;
    private javax.swing.JMenuItem jMenuItemSaveAs;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jUploadAll;
    private axoloti.menus.WindowMenu windowMenu1;
    // End of variables declaration//GEN-END:variables

    @Override
    public JFrame GetFrame() {
        return this;
    }

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

    static public void OpenBank(File f) {
        PatchBank pb = new PatchBank();
        try {
            pb.Open(f);
            pb.setVisible(true);
        } catch (IOException ex) {
            pb.Close();
            Logger.getLogger(PatchBank.class.getName()).log(Level.SEVERE, "Patchbank file not found or not accessable:{0}", f.getName());
        }
    }

    @Override
    public File getFile() {
        if (FilenamePath == null) {
            return null;
        } else {
            return new File(FilenamePath);
        }
    }

    @Override
    public ArrayList<DocumentWindow> GetChildDocuments() {
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
}
