package axoloti.shell;

import axoloti.utils.OSDetect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import axoloti.job.IJobContext;
import axoloti.job.JobContext;

/**
 *
 * @author jtaelman
 */
public class CompilePatch {

    private static String[] getEnvironment() {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(ShellTask.getEnvironment()));
        /*
        Set<String> moduleSet = this.patchController.getModel().getModules();
        if(moduleSet!=null) {
            String modules = "";
            String moduleDirs = "";
            for(String m : moduleSet) {
                modules += m + " ";
                moduleDirs +=
                    this.patchController.getModel().getModuleDir(m)
                    + " ";
            }
            list.add("MODULES=" + modules);
            list.add("MODULE_DIRS=" + moduleDirs);
        }
         */
        String vars[] = new String[list.size()];
        list.toArray(vars);
        return vars;
    }

    private static String getWorkingDir() {
        return ShellTask.getFirmwareDir();
    }

    private static String getExec() {
        if (OSDetect.getOS() == OSDetect.OS.WIN) {
            return ShellTask.getFirmwareDir() + "/compile_patch_win.bat";
        } else if (OSDetect.getOS() == OSDetect.OS.MAC) {
            return "/bin/sh ./compile_patch_osx.sh";
        } else if (OSDetect.getOS() == OSDetect.OS.LINUX) {
            return "/bin/sh ./compile_patch_linux.sh";
        } else {
            Logger.getLogger(CompilePatch.class.getName()).log(Level.SEVERE, "UPLOAD: OS UNKNOWN!");
            return null;
        }
    }

    public static void run() throws ExecutionFailedException {
        ShellTask shellTask = new ShellTask(
                getWorkingDir(),
                getExec(),
                ShellTask.getEnvironment());
        println("Start compiling patch");
        IJobContext ctx = new JobContext();
        Thread t = new Thread(() -> {
            Consumer<IJobContext> j = shellTask.getJob();
            j.accept(ctx);
        });
        t.start();
        boolean success = shellTask.isSuccess();
        if (success) {
            println("Done compiling patch");
        } else {
            println("Compiling patch failed");
            throw new ExecutionFailedException();
        }
    }

    private static void println(String s) {
        Logger.getLogger(CompilePatch.class.getName()).log(Level.INFO, s);
    }

}
