package axoloti.shell;

import axoloti.job.IJobContext;
import axoloti.job.JobContext;
import static axoloti.shell.ShellTask.getFirmwareDir;
import axoloti.utils.OSDetect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jtaelman
 */
public class CompileModule {

    private static String[] getEnvironment(String module, String moduleDir) {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(ShellTask.getEnvironment()));

        list.add("MODULE=" + module);
        list.add("MODULE_DIR=" + moduleDir);

        String vars[] = new String[list.size()];
        list.toArray(vars);
        return vars;
    }

    private static String getWorkingDir() {
        return getFirmwareDir();
    }

    private static String getExec() {
        if (OSDetect.getOS() == OSDetect.OS.WIN) {
            return getFirmwareDir() + "/compile_module_win.bat";
        } else if (OSDetect.getOS() == OSDetect.OS.MAC) {
            return "/bin/sh ./compile_module_osx.sh";
        } else if (OSDetect.getOS() == OSDetect.OS.LINUX) {
            return "/bin/sh ./compile_module_linux.sh";
        } else {
            Logger.getLogger(CompileModule.class.getName()).log(Level.SEVERE, "UPLOAD: OS UNKNOWN!");
            return null;
        }
    }

    public static void run(String module, String moduleDir) throws ExecutionFailedException {
        ShellTask shellTask = new ShellTask(
                getWorkingDir(),
                getExec(),
                getEnvironment(module, moduleDir));
        println("Start compiling module " + module);
        IJobContext ctx = new JobContext();
        Thread t = new Thread(() -> {
            Consumer<IJobContext> j = shellTask.getJob();
            j.accept(ctx);
        });
        t.start();
        boolean success = shellTask.isSuccess();
        if (success) {
            println("Done compiling module " + module);
        } else {
            println("Compiling module failed ( " + module + " ) ");
            throw new ExecutionFailedException();
        }
    }

    private static void println(String s) {
        Logger.getLogger(CompileModule.class.getName()).log(Level.INFO, s);
    }

}
