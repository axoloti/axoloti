package axoloti.shell;

import axoloti.Axoloti;
import axoloti.job.IJobContext;
import axoloti.job.JobContext;
import axoloti.target.fs.SDFileReference;
import axoloti.utils.OSDetect;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jtaelman
 */
public class CompilePatch {

    private static String getWorkingDir() {
        return Axoloti.getAPIDir();
    }

    private static String[] getExec() {
        // TODO: check path inconsistency WIN/MAC/LINUX
        String envDir = Axoloti.getEnvDir();
        String makeCmd = ShellTask.getMake();
        String platformEnvDir;
        if (OSDetect.getOS() == OSDetect.OS.WIN) {
            platformEnvDir = envDir + "/platform_win";
        } else if (OSDetect.getOS() == OSDetect.OS.MAC) {
            platformEnvDir = envDir + "/platform_osx";
        } else if (OSDetect.getOS() == OSDetect.OS.LINUX) {
            platformEnvDir = envDir + "/platform_linux";
        } else {
            Logger.getLogger(CompilePatch.class.getName()).log(Level.SEVERE, "UPLOAD: OS UNKNOWN!");
            return null;
        }
        return new String[]{
            makeCmd,
            "-I", platformEnvDir,
            "-I", envDir,
            "-f", envDir + "/patch.mk"
        };
    }

    public static CompilePatchResult run(String[] env, String patchSourceCode) throws ExecutionFailedException {
        String buildDir = Axoloti.getBuildDir();
        String fn = buildDir + "/xpatch.cpp";
        File fd = new File(fn);
        try (FileOutputStream f = new FileOutputStream(fd)) {
            f.write(patchSourceCode.getBytes());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CompilePatch.class.getName()).log(Level.SEVERE, ex.toString());
            throw new ExecutionFailedException();
        } catch (IOException ex) {
            Logger.getLogger(CompilePatch.class.getName()).log(Level.SEVERE, ex.toString());
            throw new ExecutionFailedException();
        }
        List<String> _env = new LinkedList();
        _env.addAll(Arrays.asList(ShellTask.getEnvironment()));
        _env.addAll(Arrays.asList(env));
        ShellTask shellTask = new ShellTask(
                getWorkingDir(),
                getExec(),
                _env.toArray(new String[]{}));
        IJobContext ctx = new JobContext();
        ctx.setNote("Start compiling patch");
        Thread t = new Thread(() -> {
            shellTask.doit(ctx);
        });
        String elffname = Axoloti.getBuildDir() + "/xpatch.elf";
        File f = new File(elffname);
        if (f.exists()) {
            f.delete();
        }
        t.start();
        boolean success = shellTask.isSuccess();
        if (success) {
            println("Done compiling patch");
            String fdepsfname = Axoloti.getBuildDir() + "/filedeps.txt";
            byte elfdata[];
            try {
                elfdata = Files.readAllBytes(new File(elffname).toPath());
                BufferedReader br = new BufferedReader(new FileReader(fdepsfname));
                String s1;
                String s = "";
                while ((s1 = br.readLine()) != null) {
                    s += s1 + " ";
                }
                s = s.trim();
                String[] filedeps = s.split("\\s");
                int i;
                SDFileReference sdfrs[] = new SDFileReference[filedeps.length / 2];
                int n = filedeps.length / 2;
                for (i = 0; i < n; i++) {
                    String fnlocal = filedeps[2 * i];
                    String fntarget = filedeps[2 * i + 1];
                    sdfrs[i] = new SDFileReference(new File(fnlocal), fntarget);
                }
                return new CompilePatchResult(elfdata, sdfrs, shellTask.getOutput());
            } catch (IOException ex) {
                throw new ExecutionFailedException();
            }
        } else {
            println("Compiling patch failed");
            return new CompilePatchResult(null, new SDFileReference[]{}, shellTask.getOutput());
        }
    }

    private static void println(String s) {
        Logger.getLogger(CompilePatch.class.getName()).log(Level.INFO, s);
    }

}
