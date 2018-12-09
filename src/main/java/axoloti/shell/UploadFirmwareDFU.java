package axoloti.shell;

import axoloti.utils.OSDetect;
import java.util.logging.Level;
import java.util.logging.Logger;
import axoloti.job.GlobalJobProcessor;

/**
 *
 * @author jtaelman
 */
public class UploadFirmwareDFU {

    private UploadFirmwareDFU() {
    }

    private static String getExec() {
        if (OSDetect.getOS() == OSDetect.OS.WIN) {
            return ShellTask.getRuntimeDir() + "/platform_win/upload_fw_dfu.bat";
        } else if (OSDetect.getOS() == OSDetect.OS.MAC) {
            return "/bin/sh " + ShellTask.getRuntimeDir() + "/platform_osx/upload_fw_dfu.sh";
        } else if (OSDetect.getOS() == OSDetect.OS.LINUX) {
            return "/bin/sh " + ShellTask.getRuntimeDir() + "/platform_linux/upload_fw_dfu.sh";
        } else {
            Logger.getLogger(UploadFirmwareDFU.class.getName()).log(Level.SEVERE, "UPLOAD: OS UNKNOWN!");
            return null;
        }
    }

    private static String getWorkingDir() {
        return System.getProperty(axoloti.Axoloti.FIRMWARE_DIR);
    }

    private static void run() {
        ShellTask shellTask = new ShellTask(
                getWorkingDir(),
                getExec(),
                ShellTask.getEnvironment());
        println("Start flashing firmware with DFU");

        GlobalJobProcessor.getJobProcessor().exec(shellTask.getJob());
        boolean success = shellTask.isSuccess();
        if (success) {
            println("Done flashing firmware with DFU.");
        } else {
            println("Flashing firmware failed!");
        }
    }

    public static void doit() {
        Thread thread = new Thread(() -> run());
        thread.start();
    }

    private static void println(String s) {
        Logger.getLogger(UploadFirmwareDFU.class.getName()).log(Level.INFO, s);
    }

}
