package axoloti.patchbank;

import axoloti.connection.CConnection;
import axoloti.mvc.AbstractModel;
import axoloti.property.ObjectProperty;
import axoloti.property.Property;
import axoloti.swingui.patch.PatchFrame;
import axoloti.swingui.patch.PatchViewSwing;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import qcmds.QCmdProcessor;
import qcmds.QCmdUploadFile;

/**
 *
 * @author jtaelman
 */
public class PatchBankModel extends AbstractModel {

    File file = null;

    public final static String fileExtension = ".axb";

    Boolean dirty = false;

    ArrayList<File> files;

    public final static Property FILE = new ObjectProperty("File", File.class, PatchBankModel.class);
    public final static Property FILES = new ObjectProperty("Files", ArrayList.class, PatchBankModel.class);

    @Override
    public List<Property> getProperties() {
        List<Property> list = new ArrayList<>();
        list.add(FILE);
        list.add(FILES);
        return list;
    }

    public String toRelative(File f) {
        if ((file != null) && file.isFile()) {
            Path path = Paths.get(f.getPath());
            Path pathBase = Paths.get(file.getParent());
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
        Path basePath = file.toPath();
        Path resolvedPath = basePath.getParent().resolve(s);
        return resolvedPath.toFile();
    }

    public PatchBankModel() {
        files = new ArrayList<File>();
    }

    public PatchBankModel(File f) throws IOException {
        file = f;
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

    public void Save() {
        try {
            PrintWriter pw = new PrintWriter(file);
            for (File file : files) {
                String fn = toRelative(file);
                pw.println(fn);
            }
            pw.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PatchBankModel.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
        firePropertyChange(FILE, null, file);
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<File> files) {
        this.files = files;
        firePropertyChange(FILES, null, files);
    }

    public void upload() {
        QCmdProcessor processor = QCmdProcessor.getQCmdProcessor();
        if (CConnection.GetConnection().isConnected()) {
            processor.AppendToQueue(new QCmdUploadFile(new ByteArrayInputStream(GetContents()), "/index.axb"));
        }
    }

    public void UploadOneFile(File f) {
        if (!f.isFile() || !f.canRead()) {
            return;
        }
        // todo: do this without swingui references
        PatchFrame pf = PatchViewSwing.OpenPatchInvisible(f);
        if (pf != null) {
            boolean isVisible = pf.isVisible();
            pf.getPatchController().UploadToSDCard();
            if (!isVisible) {
                pf.Close();
            }

            //FIXME: workaround waitQueueFinished bug
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                ;
            }

            try {
                QCmdProcessor.getQCmdProcessor().WaitQueueFinished();
            } catch (Exception ex) {
                Logger.getLogger(PatchBankModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void uploadAll() {
        Logger.getLogger(PatchBankModel.class.getName()).log(Level.INFO, "Uploading patch bank file");
        QCmdProcessor processor = QCmdProcessor.getQCmdProcessor();
        if (CConnection.GetConnection().isConnected()) {
            processor.AppendToQueue(new QCmdUploadFile(new ByteArrayInputStream(GetContents()), "/index.axb"));
        }

        for (File f : files) {
            Logger.getLogger(PatchBankModel.class.getName()).log(Level.INFO, "Compiling and uploading : {0}", f.getName());
            UploadOneFile(f);
        }
        Logger.getLogger(PatchBankModel.class.getName()).log(Level.INFO, "Patch bank uploaded");
    }

}
