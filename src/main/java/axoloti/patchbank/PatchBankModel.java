package axoloti.patchbank;

import axoloti.mvc.AbstractModel;
import axoloti.mvc.IModel;
import axoloti.patch.PatchController;
import axoloti.patch.PatchModel;
import axoloti.property.ListProperty;
import axoloti.property.ObjectProperty;
import axoloti.property.Property;
import axoloti.shell.ExecutionFailedException;
import axoloti.target.TargetModel;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import axoloti.job.GlobalJobProcessor;
import axoloti.job.IJobContext;

/**
 *
 * @author jtaelman
 */
public class PatchBankModel extends AbstractModel<PatchBankController> {

    private File file = null;

    public final static String FILE_EXTENSION = ".axb";

    private List<File> files = new ArrayList<>();

    public final static Property FILE = new ObjectProperty("File", File.class, PatchBankModel.class);
    public final static Property FILES = new ListProperty("Files", PatchBankModel.class);

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

    private File fromRelative(String s) {
        Path basePath = file.toPath();
        Path baseParent = basePath.getParent();
        Path resolvedPath = baseParent.resolve(s);
        return resolvedPath.toFile();
    }

    public PatchBankModel() {
    }

    public PatchBankModel(InputStream inputStream, String filename) throws IOException {
        file = new File(filename);
        BufferedReader fbs = new BufferedReader(new InputStreamReader(inputStream));
        String s;
        while ((s = fbs.readLine())
                != null) {
            File ff = fromRelative(s);
            if (ff != null) {
                files.add(ff);
            }
        }
    }

    public byte[] getContents() {
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

    public void save() {
        try (PrintWriter pw = new PrintWriter(file)) {
            for (File file : files) {
                String fn = toRelative(file);
                pw.println(fn);
            }
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

    public List<File> getFiles() {
        return Collections.unmodifiableList(files);
    }

    public void setFiles(List<File> files) {
        this.files = files;
        firePropertyChange(FILES, null, files);
    }

    public void upload() {
        TargetModel targetModel = TargetModel.getTargetModel();
        if (targetModel.getConnection().isConnected()) {
            GlobalJobProcessor.getJobProcessor().exec((ctx) -> {
                try {
                    byte b[] = getContents();
                    targetModel.upload(
                            "/index.axb",
                            new ByteArrayInputStream(b),
                            Calendar.getInstance(),
                            b.length,
                            ctx);
                } catch (IOException ex) {
                    ctx.reportException(ex);
                }
            });
        }
    }

    public void uploadOneFile(File f, IJobContext ctx) throws IOException, ExecutionFailedException, InterruptedException, ExecutionException {
        if (!f.isFile() || !f.canRead()) {
            return;
        }
        CompletableFuture<PatchController> pccf = new CompletableFuture<>();
        ctx.doInSync(() -> {
            try {
                PatchModel patchModel = PatchModel.open(f);
                pccf.complete(patchModel.getController());
            } catch (Exception ex) {
                ctx.reportException(ex);
            }
        });
        PatchController pc = pccf.get();
        if (pc != null) {
            pc.uploadToSDCard(ctx);
        }
    }

    public void uploadAll(IJobContext ctx) {
        try {
            Logger.getLogger(PatchBankModel.class.getName()).log(Level.INFO, "Uploading patch bank file");
            final byte b[] = getContents();
            TargetModel.getTargetModel().upload(
                    "/index.axb",
                    new ByteArrayInputStream(b),
                    Calendar.getInstance(),
                    b.length,
                    ctx
            );
            final int n = files.size();
            IJobContext ctxs[] = ctx.createSubContexts(n);
            for (int i = 0; i < n; i++) {
                File f = files.get(i);
                Logger.getLogger(PatchBankModel.class.getName()).log(Level.INFO, "Compiling and uploading : {0}", f.getName());
                uploadOneFile(f, ctxs[i]);
            }
            Logger.getLogger(PatchBankModel.class.getName()).log(Level.INFO, "Patch bank uploaded");
        } catch (IOException | ExecutionFailedException | InterruptedException | ExecutionException ex) {
            ctx.reportException(ex);
        }
    }

    @Override
    public PatchBankController createController() {
        return new PatchBankController(this);
    }

    @Override
    public IModel getParent() {
        return null;
    }

}
