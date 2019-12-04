/**
 * Copyright (C) 2013 - 2019 Johannes Taelman
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
import axoloti.connection.PatchLoadFailedException;
import axoloti.job.GlobalJobProcessor;
import axoloti.job.IJobContext;
import axoloti.preferences.Preferences;
import axoloti.swingui.TextEditor;
import axoloti.swingui.patch.PatchViewSwing;
import axoloti.swingui.patchbank.PatchBank;
import axoloti.target.TargetModel;
import axoloti.target.fs.SDCardInfo;
import axoloti.target.fs.SDFileInfo;
import axoloti.textdoc.TextController;
import axoloti.textdoc.TextModel;
import java.awt.Desktop;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Johannes Taelman
 */
public class FileManagerFrame extends TJFrame {

    String path = "/";

    /**
     * Creates new form FileManagerFrame
     */
    public FileManagerFrame(TargetModel targetModel) {
        super(targetModel);
        initComponents();
        jLabelSDInfo.setText("");

        jFileTable.setModel(new AbstractTableModel() {
            private final String[] columnNames = {"Name", "Type", "Size", "Date"};

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
                if (getSDCardInfo() == null) {
                    return 0;
                }
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
                    default:
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
                    if (CConnection.getConnection().isConnected()) {
                        GlobalJobProcessor.getJobProcessor().exec(ctx -> {
                            int n = droppedFiles.size();
                            IJobContext ctxs[] = ctx.createSubContexts(n);
                            for (int i = 0; i < n; i++) {
                                File f = droppedFiles.get(i);
                                try {
                                    uploadFile(CConnection.getConnection(), f, path + f.getName(), ctxs[i]);
                                } catch (FileNotFoundException ex) {
                                    Logger.getLogger(FileManagerFrame.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (IOException ex) {
                                    Logger.getLogger(FileManagerFrame.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            ctx.doInSync(() -> {
                                requestRefresh();
                            });
                        });
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
                updateButtons();
            }
        });
    }

    void setPath(String p) {
        if (p == null || p.isEmpty()) {
            path = "/";
        } else if (p.charAt(0) != '/') {
            path = '/' + p;
        } else {
            path = p;
        }
        jLabelPath.setText(path);
        jButtonDirUp.setEnabled(!path.equals("/"));
    }

    private int getSelectedRow() {
        int row = jFileTable.getSelectedRow();
        if (row < 0) {
            return row;
        } else {
            return jFileTable.convertRowIndexToModel(row);
        }
    }

    void updateButtons() {
        int row = getSelectedRow();
        if (row < 0) {
            jButtonDelete.setEnabled(false);
            jButtonOpen.setEnabled(false);
            jButtonUpload.setText("Upload ...");
            jButtonCreateDir.setText("Create directory ...");
            jButtonOpen.setText("Open");
        } else {
            jButtonDelete.setEnabled(true);
            jButtonOpen.setEnabled(true);
            final SDFileInfo fileInfo = getSDCardInfo().getFiles().get(row);
            jButtonOpen.setText("download");
            // TODO: (low priority) multiple buttons if multiple actions are possible
            for (FileExtensionHandler feh : fileExtensionHandlers) {
                String btnName = feh.takesFile(fileInfo);
                if (btnName != null) {
                    jButtonOpen.setText(btnName);
                    break;
                }
            }
        }
    }

    static void uploadFile(IConnection conn, File f, String targetFilename, IJobContext ctx) throws FileNotFoundException, IOException {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(f.lastModified());
        int size = (int) f.length();
        FileInputStream inputStream = new FileInputStream(f);
        conn.upload(targetFilename, inputStream, cal, size, ctx);
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
        jLabelPath = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jButtonDirUp = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabelSDInfo = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jButtonRefresh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jFileTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jButtonOpen = new javax.swing.JButton();
        jButtonUpload = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jButtonCreateDir = new javax.swing.JButton();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.PAGE_AXIS));

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        jPanel1.setAlignmentX(0.0F);
        jPanel1.setAlignmentY(0.0F);
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jLabelPath.setText("/");
        jLabelPath.setPreferredSize(new java.awt.Dimension(183, 16));
        jPanel1.add(jLabelPath);
        jPanel1.add(filler1);

        jButtonDirUp.setText("Up");
        jButtonDirUp.setAlignmentX(1.0F);
        jButtonDirUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDirUpActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonDirUp);

        getContentPane().add(jPanel1);

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        jPanel2.setAlignmentX(0.0F);
        jPanel2.setAlignmentY(0.0F);
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        jLabelSDInfo.setText("card info");
        jPanel2.add(jLabelSDInfo);
        jPanel2.add(filler2);

        jButtonRefresh.setText("Refresh");
        jButtonRefresh.setAlignmentX(1.0F);
        jButtonRefresh.setEnabled(false);
        jButtonRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRefreshActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonRefresh);

        getContentPane().add(jPanel2);

        jFileTable.setAutoCreateRowSorter(true);
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
        jFileTable.setAlignmentX(0.0F);
        jFileTable.getTableHeader().setReorderingAllowed(false);
        jFileTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fileTableMouseClickedHandler(evt);
            }
        });
        jScrollPane1.setViewportView(jFileTable);
        if (jFileTable.getColumnModel().getColumnCount() > 0) {
            jFileTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        }

        getContentPane().add(jScrollPane1);

        jPanel3.setAlignmentX(0.0F);
        jPanel3.setAlignmentY(1.0F);

        jButtonOpen.setText("Open");
        jButtonOpen.setAlignmentX(0.5F);
        jButtonOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOpenActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonOpen);

        jButtonUpload.setText("Upload...");
        jButtonUpload.setAlignmentX(0.5F);
        jButtonUpload.setEnabled(false);
        jButtonUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUploadActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonUpload);

        jButtonDelete.setText("Delete");
        jButtonDelete.setAlignmentX(0.5F);
        jButtonDelete.setEnabled(false);
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonDelete);

        jButtonCreateDir.setText("Create directory...");
        jButtonCreateDir.setAlignmentX(0.5F);
        jButtonCreateDir.setEnabled(false);
        jButtonCreateDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCreateDirActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonCreateDir);

        getContentPane().add(jPanel3);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    void requestRefresh() {
        IConnection conn = getDModel().getConnection();
        if (conn != null) {
            try {
                SDCardInfo sdcardinfo = conn.getFileList(path);
                setSDCardInfo(sdcardinfo);
            } catch (IOException ex) {
                Logger.getLogger(FileManagerFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void jButtonRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRefreshActionPerformed
        requestRefresh();
    }//GEN-LAST:event_jButtonRefreshActionPerformed

    private void jButtonUploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUploadActionPerformed
        IConnection conn = getDModel().getConnection();
        if (conn.isConnected()) {
            final JFileChooser fc = new JFileChooser(Preferences.getPreferences().getCurrentFileDirectory());
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                Preferences.getPreferences().setCurrentFileDirectory(fc.getCurrentDirectory().getPath());
                final File f = fc.getSelectedFile();
                if (f != null) {
                    if (!f.canRead()) {
                        Logger.getLogger(FileManagerFrame.class.getName()).log(Level.SEVERE, "Can't read file");
                        return;
                    }
                    GlobalJobProcessor.getJobProcessor().exec(ctx -> {
                        try {
                            uploadFile(conn, f, path + f.getName(), ctx);
                        } catch (FileNotFoundException ex) {
                            ctx.reportException(ex);
                        } catch (IOException ex) {
                            ctx.reportException(ex);
                        }
                    });
                }
            }
            requestRefresh();
        }
    }//GEN-LAST:event_jButtonUploadActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        // RequestRefresh();
    }//GEN-LAST:event_formWindowActivated

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    }//GEN-LAST:event_formWindowClosing

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        int rowIndex = getSelectedRow();
        if (rowIndex >= 0) {
            SDFileInfo f = getSDCardInfo().getFiles().get(rowIndex);
            if (!f.isDirectory()) {
                try {
                    IConnection conn = getDModel().getConnection();
                    conn.deleteFile(path + f.getFilename());
                } catch (IOException ex) {
                    Logger.getLogger(FileManagerFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                String ff = f.getFilename();
                if (ff.endsWith("/")) {
                    ff = ff.substring(0, ff.length() - 1);
                }
                try {
                    IConnection conn = getDModel().getConnection();
                    conn.deleteFile(path + ff);
                } catch (IOException ex) {
                    if ("FR_DENIED".equals(ex.getMessage())) {
                        JOptionPane.showMessageDialog(this, "Failed to delete directory, not empty?");
                    } else {
                        Logger.getLogger(FileManagerFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        jFileTable.clearSelection();
        requestRefresh();
    }//GEN-LAST:event_jButtonDeleteActionPerformed

    private void jButtonCreateDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCreateDirActionPerformed
        String fn = JOptionPane.showInputDialog(this, "Directory name?");

        if (fn != null && !fn.isEmpty()) {
            final String fullFileName = path + fn;
            IConnection conn = getDModel().getConnection();
            try {
                conn.createDirectory(fullFileName, Calendar.getInstance());
            } catch (IOException ex) {
                Logger.getLogger(FileManagerFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        updateButtons();
        requestRefresh();
    }//GEN-LAST:event_jButtonCreateDirActionPerformed

    private static class ByteBufferBackedInputStream extends InputStream {

        private final ByteBuffer buf;

        ByteBufferBackedInputStream(ByteBuffer buf) {
            this.buf = buf;
        }

        @Override
        public int read() throws IOException {
            if (!buf.hasRemaining()) {
                return -1;
            }
            return buf.get() & 0xFF;
        }

        @Override
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

    private interface FileExtensionHandler {
        abstract String takesFile(SDFileInfo fileInfo);
        abstract void open(SDFileInfo fileInfo);
    }

    private final class TextFileHandler implements FileExtensionHandler {
        @Override
        public String takesFile(SDFileInfo fileInfo) {
            if (fileInfo.getExtension().equals("txt")) {
                return "Open text";
            }
            return null;
        }

        @Override
        public void open(SDFileInfo fileInfo) {
            GlobalJobProcessor.getJobProcessor().exec((ctx) -> {
                try {
                    final IConnection conn = getDModel().getConnection();
                    final ByteBuffer byteBuffer = conn.download(path + fileInfo.getFilename(), ctx);
                    String s = StandardCharsets.UTF_8.decode(byteBuffer).toString();
                    ctx.doInSync(() -> {
                        TextModel textModel = new TextModel(s);
                        TextController textController = new TextController(textModel);
                        TextEditor textEditor = new TextEditor(TextModel.TEXT, textModel, null);
                        textController.addView(textEditor);
                        textEditor.setTitle(fileInfo.getFilename());
                        textEditor.setVisible(true);
                        textEditor.toFront();
                    });
                } catch (IOException ex) {
                    Logger.getLogger(FileManagerFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    };

    private final class PatchBankHandler implements FileExtensionHandler {

        @Override
        public String takesFile(SDFileInfo fileInfo) {
            if (fileInfo.getExtension().equals("axb")) {
                return "Open patch bank";
            }
            return null;
        }

        @Override
        public void open(SDFileInfo fileInfo) {
            GlobalJobProcessor.getJobProcessor().exec((ctx) -> {
                try {
                    final IConnection conn = getDModel().getConnection();
                    final ByteBuffer byteBuffer = conn.download(path + fileInfo.getFilename(), ctx);
                    InputStream inputStream = new ByteBufferBackedInputStream(byteBuffer);
                    ctx.doInSync(() -> {
                        PatchBank.openPatchBankEditor(inputStream, path + fileInfo.getFilenameNoExtension());
                    });
                } catch (IOException ex) {
                    Logger.getLogger(FileManagerFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }

    private final class PatchHandler implements FileExtensionHandler {

        @Override
        public String takesFile(SDFileInfo fileInfo) {
            if (fileInfo.getExtension().equals("axp")) {
                return "Open patch";
            }
            return null;
        }

        @Override
        public void open(SDFileInfo fileInfo) {
            GlobalJobProcessor.getJobProcessor().exec((ctx) -> {
                try {
                    final IConnection conn = getDModel().getConnection();
                    final ByteBuffer byteBuffer = conn.download(path + fileInfo.getFilename(), null);
                    InputStream inputStream = new ByteBufferBackedInputStream(byteBuffer);
                    ctx.doInSync(() -> {
                        PatchViewSwing.openPatch(fileInfo.getFilenameNoExtension(), inputStream);
                    });
                } catch (IOException ex) {
                    Logger.getLogger(FileManagerFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }

    private final class ElfHandler implements FileExtensionHandler {

        @Override
        public String takesFile(SDFileInfo fileInfo) {
            if (fileInfo.getExtension().equals("elf")) {
                return "Run patch";
            }
            return null;
        }

        @Override
        public void open(SDFileInfo fileInfo) {
            String patchname = fileInfo.getFilename();
            try {
                final IConnection conn = getDModel().getConnection();
                conn.transmitStart(path + patchname, null);
            } catch (IOException ex) {
                Logger.getLogger(FileManagerFrame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (PatchLoadFailedException ex) {
                Logger.getLogger(FileManagerFrame.class.getName()).log(Level.SEVERE, null, ex.getMessage());
            }

        }

    }

    private final class MidiRoutingHandler implements FileExtensionHandler {

        @Override
        public String takesFile(SDFileInfo fileInfo) {
            if (fileInfo.getExtension().equals("axr")) {
                return "Open midi routing table";
            }
            return null;
        }

        @Override
        public void open(SDFileInfo fileInfo) {
            GlobalJobProcessor.getJobProcessor().exec((ctx) -> {
                try {
                    final IConnection conn = getDModel().getConnection();
                    final ByteBuffer mem = conn.download(path + fileInfo.getFilename(), ctx);
                    StringBuilder sb = new StringBuilder();
                    sb.append("midi routing file contents:\n");
                    while (mem.remaining() > 0) {
                        int i = mem.getInt();
                        for (int j = 0; j < 32; j++) {
                            sb.append((i & 1) == 1 ? "1" : "0");
                            i >>= 1;
                        }
                        sb.append("\n");
                    }
                    sb.append("\n");
                    String s = sb.toString();
                    ctx.doInSync(() -> {
                        TextModel textModel = new TextModel(s);
                        TextController textController = new TextController(textModel);
                        TextEditor textEditor = new TextEditor(TextModel.TEXT, textModel, null);
                        textController.addView(textEditor);
                        textEditor.setTitle(fileInfo.getFilename());
                        textEditor.setVisible(true);
                        textEditor.toFront();
                    });
                } catch (IOException ex) {
                    Logger.getLogger(FileManagerFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }

    private final class DownloadHandler implements FileExtensionHandler {

        @Override
        public String takesFile(SDFileInfo fileInfo) {
            if (!fileInfo.isDirectory()) {
                return "Download";
            }
            return null;
        }

        @Override
        public void open(SDFileInfo fileInfo) {
            GlobalJobProcessor.getJobProcessor().exec((ctx) -> {
                try {
                    final IConnection conn = getDModel().getConnection();
                    final ByteBuffer mem = conn.download(path + fileInfo.getFilename(), ctx);
                    mem.rewind();
                    Path tempDirWithPrefix = Files.createTempDirectory("ax_");
                    File f1 = new File(tempDirWithPrefix.toString() + "/" + fileInfo.getFilename());
                    FileOutputStream fos = new FileOutputStream(f1);
                    WritableByteChannel channel = Channels.newChannel(fos);
                    channel.write(mem);
                    // f1.deleteOnExit(); // TODO: (low priority) enable deleteOnExit - with care!
                    if (Desktop.isDesktopSupported()) {
                        Desktop desktop = Desktop.getDesktop();
                        desktop.open(tempDirWithPrefix.toFile());
                        // TODO: (low priority) filemanager: watch folder with tmp downloads, auto-upload
                        // WatchService watcher = FileSystems.getDefault().newWatchService();
                        // tempDirWithPrefix.register(watcher, ENTRY_MODIFY);
                    } else {
                        System.out.println("desktop is not supported");
                    }

                } catch (IOException ex) {
                    Logger.getLogger(FileManagerFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }

    private final class DirHandler implements FileExtensionHandler {

        @Override
        public String takesFile(SDFileInfo fileInfo) {
            if (fileInfo.isDirectory()) {
                return "Open directory";
            }
            return null;
        }

        @Override
        public void open(SDFileInfo fileInfo) {
            String dirname = fileInfo.getFilename();
            setPath(path + dirname);
            requestRefresh();
        }

    }

    private final FileExtensionHandler fileExtensionHandlers[] = {
        new PatchBankHandler(),
        new PatchHandler(),
        new ElfHandler(),
        new MidiRoutingHandler(),
        new DirHandler(),
        new TextFileHandler(),
        new DownloadHandler()
    };

    private void open() {
        int rowIndex = getSelectedRow();
        if (rowIndex >= 0) {
            final SDFileInfo f = getSDCardInfo().getFiles().get(rowIndex);
            final IConnection conn = getDModel().getConnection();
            for(FileExtensionHandler feh: fileExtensionHandlers) {
                if (feh.takesFile(f) != null) {
                    feh.open(f);
                    return;
                }
            }
        }
    }


    private void jButtonOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOpenActionPerformed
        open();
    }//GEN-LAST:event_jButtonOpenActionPerformed

    private void jButtonDirUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDirUpActionPerformed

        String path1 = path;
        if (path.charAt(path.length() - 1) == '/') {
            path1 = path.substring(0, path.length() - 1);
        }
        int i = path1.lastIndexOf('/');
        if (i > 0) {
            setPath(path1.substring(0, i) + "/");
        } else {
            setPath("/");
        }
        requestRefresh();
    }//GEN-LAST:event_jButtonDirUpActionPerformed

    private void fileTableMouseClickedHandler(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileTableMouseClickedHandler
        if (evt.getClickCount() == 2 && getSelectedRow() != -1) {
            jButtonOpenActionPerformed(new ActionEvent(ERROR, WIDTH, path));
        }
    }//GEN-LAST:event_fileTableMouseClickedHandler

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JButton jButtonCreateDir;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonDirUp;
    private javax.swing.JButton jButtonOpen;
    private javax.swing.JButton jButtonRefresh;
    private javax.swing.JButton jButtonUpload;
    private javax.swing.JTable jFileTable;
    private javax.swing.JLabel jLabelPath;
    private javax.swing.JLabel jLabelSDInfo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    void showConnect(boolean status) {
        jButtonRefresh.setEnabled(status);
        jButtonUpload.setEnabled(status);
        jFileTable.setEnabled(status);
        jLabelSDInfo.setText("");
        jButtonDelete.setEnabled(status);
        jButtonCreateDir.setEnabled(status);
        jButtonDirUp.setEnabled(status);
        if (status) {
            path = "/";
            requestRefresh();
        } else {
            setSDCardInfo(new SDCardInfo(0, 0, 0, Collections.emptyList()));
        }
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (TargetModel.CONNECTION.is(evt)) {
            showConnect(evt.getNewValue() != null);
        } else if (TargetModel.HAS_SDCARD.is(evt)) {
            Boolean b = (Boolean) evt.getNewValue();
            showConnect(b);
        }
    }

    SDCardInfo sdcardInfo;

    private SDCardInfo getSDCardInfo() {
        return sdcardInfo;
    }

    private void setSDCardInfo(SDCardInfo sdcardInfo) {
        this.sdcardInfo = sdcardInfo;
        if (sdcardInfo != null) {
            int clusters = sdcardInfo.getClusters();
            int clustersize = sdcardInfo.getClustersize();
            int sectorsize = sdcardInfo.getSectorsize();
            jLabelSDInfo.setText("Free : " + ((long) clusters * (long) clustersize * (long) sectorsize / (1024 * 1024)) + "MB, Cluster size = " + clustersize * sectorsize);
        }
        ((AbstractTableModel) jFileTable.getModel()).fireTableDataChanged();
    }

}
