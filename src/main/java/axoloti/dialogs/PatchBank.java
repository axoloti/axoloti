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
import axoloti.MainFrame;
import axoloti.USBBulkConnection;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import qcmds.QCmdProcessor;
import qcmds.QCmdUploadFile;

/**
 *
 * @author jtaelman
 */
public class PatchBank extends javax.swing.JFrame implements DocumentWindow, ConnectionStatusListener {

    String FilenamePath = null;

    /**
     * Creates new form PatchBank
     */
    public PatchBank() {
        initComponents();
        setIconImage(new ImageIcon(getClass().getResource("/resources/axoloti_icon.png")).getImage());
        DocumentWindowList.RegisterWindow(this);
        USBBulkConnection.GetConnection().addConnectionStatusListener(this);
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        for (int i = 0; i < 128; i++) {
            model.addRow(new Object[]{i, ""});
        }
    }

    public byte[] GetContents() {
        ByteBuffer data = ByteBuffer.allocateDirect(128 * 256);
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        for (int i = 0; i < 128; i++) {
            String fn = (String) model.getValueAt(i, 1);
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

    void Open(File f) {
        InputStream s;
        try {
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            s = new FileInputStream(f);

            for (int i = 0; i < 128; i++) {
                model.addRow(new Object[]{i, ""});
            }
            s.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PatchBank.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PatchBank.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void Save(File f) {
        FileOutputStream fo;
        try {
            fo = new FileOutputStream(f);
            fo.write(GetContents());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PatchBank.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PatchBank.class.getName()).log(Level.SEVERE, null, ex);
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
        if (ext.equalsIgnoreCase(".axb")) {
            fc.setFileFilter(axb);
        }

        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String filterext = ".axb";
            if (fc.getFileFilter() == axb) {
                filterext = ".axb";
            }

            File fileToBeSaved = fc.getSelectedFile();
            ext = "";
            String fname = fileToBeSaved.getAbsolutePath();
            dot = fname.lastIndexOf('.');
            if (dot > 0 && fname.length() > dot + 3) {
                ext = fname.substring(dot);
            }

            if (!ext.equalsIgnoreCase(".axb")) {
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
            MainFrame.prefs.setCurrentFileDirectory(fileToBeSaved.getPath());
            Save(fileToBeSaved);
        }
    }

    boolean isDirty() {
        return true;
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemSave = new javax.swing.JMenuItem();
        jMenuItemSaveAs = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        windowMenu1 = new axoloti.menus.WindowMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jButtonUploadBank)
                .addContainerGap(119, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonUploadBank))
                .addContainerGap(23, Short.MAX_VALUE))
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
        jTable1.setColumnSelectionAllowed(true);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        getContentPane().add(jScrollPane1);

        jMenu1.setText("File");

        jMenuItemSave.setText("Save");
        jMenuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemSave);

        jMenuItemSaveAs.setText("Save as...");
        jMenuItemSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveAsActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemSaveAs);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);
        jMenuBar1.add(windowMenu1);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonUploadBankActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUploadBankActionPerformed
        QCmdProcessor processor = MainFrame.mainframe.getQcmdprocessor();
        if (USBBulkConnection.GetConnection().isConnected()) {
            processor.AppendToQueue(new QCmdUploadFile(new ByteArrayInputStream(GetContents()), "index.axb"));
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonUploadBank;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItemSave;
    private javax.swing.JMenuItem jMenuItemSaveAs;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private axoloti.menus.WindowMenu windowMenu1;
    // End of variables declaration//GEN-END:variables

    @Override
    public JFrame GetFrame() {
        return this;
    }

    @Override
    public void ShowConnect() {
        jButtonUploadBank.setEnabled(true);
    }

    @Override
    public void ShowDisconnect() {
        jButtonUploadBank.setEnabled(false);
    }

}
