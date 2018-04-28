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
package axoloti.swingui.target;

import axoloti.connection.CConnection;
import axoloti.connection.IConnection;
import axoloti.preferences.Preferences;
import axoloti.swingui.TextEditor;
import axoloti.swingui.patch.PatchViewSwing;
import axoloti.swingui.patchbank.PatchBank;
import axoloti.target.TargetController;
import axoloti.target.TargetModel;
import axoloti.target.fs.SDCardInfo;
import axoloti.target.fs.SDFileInfo;
import axoloti.textdoc.TextController;
import axoloti.textdoc.TextModel;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import qcmds.QCmdCreateDirectory;
import qcmds.QCmdDeleteFile;
import qcmds.QCmdGetFileContents;
import qcmds.QCmdGetFileList;
import qcmds.QCmdProcessor;
import qcmds.QCmdStart;
import qcmds.QCmdStop;
import qcmds.QCmdUploadFile;

/**
 *
 * @author Johannes Taelman
 */
public class FileManagerFrame extends TJFrame {

    /**
     * Creates new form FileManagerFrame
     */
    public FileManagerFrame(TargetController controller) {
        super(controller);
        initComponents();
        jLabelSDInfo.setText("");

        jFileTable.setModel(new AbstractTableModel() {
            private String[] columnNames = {"Name", "Type", "Size", "Date"};

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
                return String.class;
            }

            @Override
            public int getRowCount() {
                return getSDCardInfo().getFiles().size();
            }

            @Override
            public void setValueAt(Object value, int rowIndex, int columnIndex) {
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                Object returnValue = null;

                switch (columnIndex) {
                    case 0: {
                        SDFileInfo f = getSDCardInfo().getFiles().get(rowIndex);
                        if (f.isDirectory()) {
                            returnValue = f.getFilename();
                        } else {
                            returnValue = f.getFilenameNoExtension();
                        }
                    }
                    break;
                    case 1: {
                        SDFileInfo f = getSDCardInfo().getFiles().get(rowIndex);
                        if (f.isDirectory()) {
                            returnValue = "";
                        } else {
                            returnValue = f.getExtension();
                        }
                    }
                    break;
                    case 2: {
                        SDFileInfo f = getSDCardInfo().getFiles().get(rowIndex);
                        if (f.isDirectory()) {
                            returnValue = "";
                        } else {
                            int size = f.getSize();
                            if (size < 10240) {
                                returnValue = "" + size + "  bytes";
                            } else if (size < 10240 * 1024) {
                                returnValue = "" + (size / 1024) + " kB";
                            } else {
                                returnValue = "" + (size / (1024 * 1024)) + " MB";
                            }
                        }
                    }
                    break;
                    case 3: {
                        Calendar c = getSDCardInfo().getFiles().get(rowIndex).getTimestamp();
                        if (c.get(Calendar.YEAR) > 1979) {
                            returnValue = c.getTime().toString();
                        } else {
                            returnValue = "";
                        }
                    }
                    break;
                }

                return returnValue;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });

        jFileTable.setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    QCmdProcessor processor = QCmdProcessor.getQCmdProcessor();
                    if (CConnection.GetConnection().isConnected()) {
                        for (File f : droppedFiles) {
                            System.out.println(f.getName());
                            if (!f.canRead()) {
                                Logger.getLogger(FileManagerFrame.class.getName()).log(Level.SEVERE, "Can't read file");
                            } else {
                                processor.AppendToQueue(new QCmdUploadFile(f, f.getName()));
                            }
                        }
                        RequestRefresh();
                    }
                } catch (UnsupportedFlavorException ex) {
                    Logger.getLogger(FileManagerFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(FileManagerFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        jFileTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jFileTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                UpdateButtons();
            }
        });
    }

    void UpdateButtons() {
        int row = jFileTable.getSelectedRow();
        if (row < 0) {
            jButtonDelete.setEnabled(false);
            jButtonOpen.setEnabled(false);
            ButtonUploadDefaultName();
        } else {
            jButtonDelete.setEnabled(true);
            SDFileInfo f = getSDCardInfo().getFiles().get(row);
            if (f != null && f.isDirectory()) {
                jButtonUpload.setText("Upload to " + f.getFilename() + " ...");
                jButtonCreateDir.setText("Create directory in " + f.getFilename() + " ...");
                jButtonOpen.setEnabled(false);
            } else {
                ButtonUploadDefaultName();
                jButtonOpen.setEnabled(true);
            }
        }
    }

    void ButtonUploadDefaultName() {
        jButtonUpload.setText("Upload ...");
        jButtonCreateDir.setText("Create directory ...");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jFileTable = new javax.swing.JTable();
        jButton1Refresh = new javax.swing.JButton();
        jLabelSDInfo = new javax.swing.JLabel();
        jButtonUpload = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jButtonCreateDir = new javax.swing.JButton();
        jButtonOpen = new javax.swing.JButton();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jFileTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Type", "Size", "Date"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jFileTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jFileTable);
        if (jFileTable.getColumnModel().getColumnCount() > 0) {
            jFileTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        }

        jButton1Refresh.setText("Refresh");
        jButton1Refresh.setEnabled(false);
        jButton1Refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1RefreshActionPerformed(evt);
            }
        });

        jLabelSDInfo.setText("jLabel1");

        jButtonUpload.setText("Upload...");
        jButtonUpload.setEnabled(false);
        jButtonUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUploadActionPerformed(evt);
            }
        });

        jButtonDelete.setText("Delete");
        jButtonDelete.setEnabled(false);
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });

        jButtonCreateDir.setText("Create directory...");
        jButtonCreateDir.setEnabled(false);
        jButtonCreateDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCreateDirActionPerformed(evt);
            }
        });

        jButtonOpen.setText("Open");
        jButtonOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOpenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jButton1Refresh)
                .addGap(29, 29, 29)
                .addComponent(jLabelSDInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonOpen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonDelete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonUpload)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonCreateDir)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1Refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelSDInfo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonDelete)
                    .addComponent(jButtonUpload)
                    .addComponent(jButtonCreateDir)
                    .addComponent(jButtonOpen))
                .addGap(5, 5, 5))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    void RequestRefresh() {
        if (CConnection.GetConnection().isConnected()) {
            CConnection.GetConnection().AppendToQueue(new QCmdStop());
            CConnection.GetConnection().WaitSync();
            CConnection.GetConnection().AppendToQueue(new QCmdGetFileList());
        }
    }

    private void jButton1RefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1RefreshActionPerformed
        RequestRefresh();
    }//GEN-LAST:event_jButton1RefreshActionPerformed

    private void jButtonUploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUploadActionPerformed
        QCmdProcessor processor = QCmdProcessor.getQCmdProcessor();
        String dir = "/";
        int rowIndex = jFileTable.getSelectedRow();
        if (rowIndex >= 0) {
            SDFileInfo f = getSDCardInfo().getFiles().get(rowIndex);
            if (f != null && f.isDirectory()) {
                dir = f.getFilename();
            }
        }
        if (CConnection.GetConnection().isConnected()) {
            final JFileChooser fc = new JFileChooser(Preferences.getPreferences().getCurrentFileDirectory());
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                Preferences.getPreferences().setCurrentFileDirectory(fc.getCurrentDirectory().getPath());
                File f = fc.getSelectedFile();
                if (f != null) {
                    if (!f.canRead()) {
                        Logger.getLogger(FileManagerFrame.class.getName()).log(Level.SEVERE, "Can't read file");
                        return;
                    }
                    processor.AppendToQueue(new QCmdUploadFile(f, dir + f.getName()));
                }
            }
        }
    }//GEN-LAST:event_jButtonUploadActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        // RequestRefresh();
    }//GEN-LAST:event_formWindowActivated

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    }//GEN-LAST:event_formWindowClosing

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        int rowIndex = jFileTable.getSelectedRow();
        QCmdProcessor processor = QCmdProcessor.getQCmdProcessor();
        if (rowIndex >= 0) {
            SDFileInfo f = getSDCardInfo().getFiles().get(rowIndex);
            if (!f.isDirectory()) {
                processor.AppendToQueue(new QCmdDeleteFile(f.getFilename()));
            } else {
                String ff = f.getFilename();
                if (ff.endsWith("/")) {
                    ff = ff.substring(0, ff.length() - 1);
                }
                processor.AppendToQueue(new QCmdDeleteFile(ff));
            }
        }
        jFileTable.clearSelection();
    }//GEN-LAST:event_jButtonDeleteActionPerformed

    private void jButtonCreateDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCreateDirActionPerformed
        String dir = "/";
        int rowIndex = jFileTable.getSelectedRow();
        if (rowIndex >= 0) {
            SDFileInfo f = getSDCardInfo().getFiles().get(rowIndex);
            if (f != null && f.isDirectory()) {
                dir = f.getFilename();
            }
        }
        String fn = JOptionPane.showInputDialog(this, "Directory name?");
        if (fn != null && !fn.isEmpty()) {
            QCmdProcessor processor = QCmdProcessor.getQCmdProcessor();
            processor.AppendToQueue(new QCmdCreateDirectory(dir + fn));
        }
        UpdateButtons();
    }//GEN-LAST:event_jButtonCreateDirActionPerformed

    class ByteBufferBackedInputStream extends InputStream {

        ByteBuffer buf;

        public ByteBufferBackedInputStream(ByteBuffer buf) {
            this.buf = buf;
        }

        public int read() throws IOException {
            if (!buf.hasRemaining()) {
                return -1;
            }
            return buf.get() & 0xFF;
        }

        public int read(byte[] bytes, int off, int len)
                throws IOException {
            if (!buf.hasRemaining()) {
                return -1;
            }

            len = Math.min(len, buf.remaining());
            buf.get(bytes, off, len);
            return len;
        }
    }

    private void jButtonOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOpenActionPerformed
        int rowIndex = jFileTable.getSelectedRow();
        QCmdProcessor processor = QCmdProcessor.getQCmdProcessor();
        if (rowIndex >= 0) {
            SDFileInfo f = getSDCardInfo().getFiles().get(rowIndex);
            processor.AppendToQueue(new QCmdGetFileContents(f.getFilename(), new IConnection.MemReadHandler() {
                @Override
                public void Done(ByteBuffer mem) {
                    if (mem == null) {
                        Logger.getLogger(FileManagerFrame.class.getName()).log(Level.SEVERE, "Open: failed");
                        return;
                    }
                    if (f.getExtension().equals("txt")) {
                        String s = "";
                        while (mem.remaining() > 0) {
                            s += (char) mem.get();
                        }
                        TextModel textModel = new TextModel(s);
                        TextController textController = new TextController(textModel);
                        TextEditor textEditor = new TextEditor(TextModel.TEXT, textController, null);
                        textController.addView(textEditor);
                        textEditor.setTitle(f.getFilename());
                        textEditor.setVisible(true);
                        textEditor.toFront();
                    } else if (f.getExtension().equals("axb")) {
                        String patchname = f.getFilename();
                        if (patchname.charAt(0) == '/') {
                            patchname = patchname.substring(1);
                        }
                        InputStream inputStream = new ByteBufferBackedInputStream(mem);
                        PatchBank.OpenPatchBankEditor(inputStream, f.getFilename());
                    } else if (f.getFilename().endsWith("/patch.bin")) {
                        String patchname = f.getFilename();
                        processor.AppendToQueue(new QCmdStart(patchname));
                    } else if (f.getFilename().endsWith("/patch.axp")) {
                        // convert "/xyz/patch.axp" into "xyz.axp"
                        String patchname = f.getFilename().substring(0, f.getFilename().length() - 10) + ".axp";
                        if (patchname.charAt(0) == '/') {
                            patchname = patchname.substring(1);
                        }
                        InputStream input = new ByteBufferBackedInputStream(mem);
                        PatchViewSwing.OpenPatch(patchname, input);
                    } else if (f.getExtension().equals("axr")) {
                        System.out.println("midi routing file contents:");
                        while (mem.remaining() > 0) {
                            int i = mem.getInt();
                            for (int j = 0; j < 32; j++) {
                                System.out.print((i & 1) == 1 ? "1" : "0");
                                i = i>>1;
                            }
                            System.out.println();
                        }
                        System.out.println();
                    } else {
                        // TODO: write to temp folder rather than cwd?
                        // TODO: file selection dialog?
                        mem.rewind();
                        System.out.println("file contents written to download.txt");
                        File f1 = new File("download.txt");
                        try {
                            FileOutputStream fos = new FileOutputStream(f1);
                            WritableByteChannel channel = Channels.newChannel(fos);
                            channel.write(mem);
                            fos.close();
                        } catch (IOException ex) {
                            Logger.getLogger(FileManagerFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        mem.rewind();
                        System.out.println("file contents:");
                        int i=0;
                        while (mem.remaining() > 0) {
                            System.out.print((char) mem.get());
//                            System.out.print(String.format("%02X\n", mem.get()));
                            if (i > 100) {
                                System.out.println("...truncated");
                                break;
                            }
                        }
                        System.out.println();
                    }
                }
            }));
        }
    }//GEN-LAST:event_jButtonOpenActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1Refresh;
    private javax.swing.JButton jButtonCreateDir;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonOpen;
    private javax.swing.JButton jButtonUpload;
    private javax.swing.JTable jFileTable;
    private javax.swing.JLabel jLabelSDInfo;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    void ShowConnect(boolean status) {
        jButton1Refresh.setEnabled(status);
        jButtonUpload.setEnabled(status);
        jFileTable.setEnabled(status);
        jLabelSDInfo.setText("");
        jButtonDelete.setEnabled(status);
        jButtonCreateDir.setEnabled(status);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (TargetModel.CONNECTION.is(evt)) {
            ShowConnect(evt.getNewValue() != null);
        } else if (TargetModel.HAS_SDCARD.is(evt)) {
            Boolean b = (Boolean) evt.getNewValue();
            ShowConnect(b);
        } else if (TargetModel.SDCARDINFO.is(evt)) {
            int clusters = getSDCardInfo().getClusters();
            int clustersize = getSDCardInfo().getClustersize();
            int sectorsize = getSDCardInfo().getSectorsize();
            jLabelSDInfo.setText("Free : " + ((long) clusters * (long) clustersize * (long) sectorsize / (1024 * 1024)) + "MB, Cluster size = " + clustersize * sectorsize);
            ((AbstractTableModel) jFileTable.getModel()).fireTableDataChanged();
        }
    }

    private SDCardInfo getSDCardInfo() {
        return getModel().getSDCardInfo();
    }

}
