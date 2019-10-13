package axoloti.shell;

import axoloti.Axoloti;
import axoloti.job.IJob;
import axoloti.job.IJobContext;
import axoloti.preferences.Preferences;
import axoloti.utils.OSDetect;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jtaelman
 */
public class ShellTask {

    final private String workingDirectory;
    final private String[] cmdarray;
    final private String[] environment;

    public ShellTask(String workingDirectory, String[] cmdarray, String[] environment) {
        this.workingDirectory = workingDirectory;
        this.cmdarray = cmdarray;
        this.environment = environment;
    }

    public IJob getJob() {
        return (ctx) -> doit(ctx);
    }

    public void doit(IJobContext ctx) {
        try {
            // Logger.getLogger(ShellTask.class.getName()).log(Level.INFO, "Working dir = {0}", workingDirectory);
            // Logger.getLogger(ShellTask.class.getName()).log(Level.INFO, "cmdarray = {0}", cmdarray);
            // Logger.getLogger(ShellTask.class.getName()).log(Level.INFO, "environment = {0}", environment);
            run(workingDirectory, cmdarray, environment);
        } catch (Exception ex) {
            ctx.reportException(ex);
        }
    }

    private static class StreamHandlerThread implements Runnable {

        private final InputStream in;

        StreamHandlerThread(InputStream in) {
            this.in = in;
        }

        @Override
        public void run() {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            try {
                line = br.readLine();
                while (line != null) {
                    if (line.contains("error")) {
                        Logger.getLogger(ShellTask.class.getName()).log(Level.SEVERE, "{0}", line);
                    } else {
                        Logger.getLogger(ShellTask.class.getName()).log(Level.INFO, "{0}", line);
                    }
                    line = br.readLine();
                }
            } catch (IOException ex) {
                Logger.getLogger(ShellTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    public static String getMake() {
        if (OSDetect.getOS() == OSDetect.OS.WIN) {
            return Axoloti.getReleaseDir() + "/platform_win/bin/make.exe";
        }
        return "make";
    }

    public static String[] getEnvironment() {
        ArrayList<String> list = new ArrayList<>();
        Map<String, String> env = System.getenv();
        String sysPath = "";
        for (String v : env.keySet()) {
            if ("PATH".equals(v)) {
                sysPath = env.get(v);
            } else if ("Path".equals(v)) {
                sysPath = env.get(v);
            } else {
                list.add((v + "=" + env.get(v)));
            }
        }
        String axPath;
        if (OSDetect.getOS() == OSDetect.OS.WIN) {
            axPath = Axoloti.getReleaseDir() + "/platform_win/bin;C:/Program Files (x86)/GNU Tools Arm Embedded/7 2018-q2-update/bin;" + Preferences.getPreferences().getPath();
        } else if (OSDetect.getOS() == OSDetect.OS.MAC) {
            axPath = Axoloti.getReleaseDir() + "/platform_osx/bin:/Applications/gcc-arm-none-eabi-7-2018-q2-update/bin:" + Preferences.getPreferences().getPath();
        } else if (OSDetect.getOS() == OSDetect.OS.LINUX) {
            // on linux we can use "make" etc from system path
            axPath = Axoloti.getReleaseDir() + "/platform_linux/bin:" + sysPath;
        } else {
            Logger.getLogger(ShellTask.class.getName()).log(Level.SEVERE, "UPLOAD: OS UNKNOWN!");
            return null;
        }
        list.add(("PATH=" + axPath));
        list.add(("Path=" + axPath));
        list.add((axoloti.Axoloti.HOME_DIR + "=" + Axoloti.getHomeDir()));
        list.add((axoloti.Axoloti.RELEASE_DIR + "=" + Axoloti.getReleaseDir()));
        list.add((axoloti.Axoloti.API_DIR + "=" + Axoloti.getAPIDir()));
        list.add((axoloti.Axoloti.ENV_DIR + "=" + Axoloti.getEnvDir()));

        String vars[] = list.toArray(new String[0]);
        return vars;
    }

    private final CompletableFuture<Boolean> success = new CompletableFuture<>();

    private void run(
            String workingDirectory,
            String[] cmdarray,
            String[] environment) throws ExecutionFailedException {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process p1 = runtime.exec(cmdarray, environment, new File(workingDirectory));
            Thread thd_out = new Thread(new StreamHandlerThread(p1.getInputStream()));
            thd_out.start();
            Thread thd_err = new Thread(new StreamHandlerThread(p1.getErrorStream()));
            thd_err.start();
            p1.waitFor();
            thd_out.join();
            thd_err.join();
            success.complete(p1.exitValue() == 0);
            if (p1.exitValue() != 0) {
                Logger.getLogger(ShellTask.class.getName()).log(Level.SEVERE, "shell task failed, exit value: {0}", p1.exitValue());
                throw new ExecutionFailedException();
            }
        } catch (InterruptedException | IOException ex) {
            Logger.getLogger(ShellTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isSuccess() {
        try {
            return success.get();
        } catch (InterruptedException | ExecutionException ex) {
            throw new IllegalStateException(ex);
        }
    }


}
