package axoloti.shell;

import axoloti.Axoloti;
import axoloti.job.GlobalJobProcessor;
import axoloti.utils.OSDetect;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jtaelman
 */
public class UploadFirmwareDFU {

    private UploadFirmwareDFU() {
    }

    private static String[] getExec(String firmwareFilename) {
        String dfu_util_filename = "";
        if (OSDetect.getOS() == OSDetect.OS.WIN) {
            dfu_util_filename = Axoloti.getReleaseDir() + "/platform_win/bin/dfu-util";
        } else if (OSDetect.getOS() == OSDetect.OS.MAC) {
            dfu_util_filename = Axoloti.getReleaseDir() + "/platform_osx/bin/dfu-util";
        } else if (OSDetect.getOS() == OSDetect.OS.LINUX) {
            dfu_util_filename = Axoloti.getReleaseDir() + "/platform_linux/bin/dfu-util";
        } else {
            Logger.getLogger(UploadFirmwareDFU.class.getName()).log(Level.SEVERE, "UPLOAD: OS UNKNOWN!");
            return null;
        }
        return new String[]{dfu_util_filename,
            "--device", "0483:df11",
            "-i", "0",
            "-a", "0",
            "-D", firmwareFilename,
            "--dfuse-address=0x08000000:leave"};
    }

    private static String getWorkingDir() {
        return new File(Axoloti.getFirmwareFilename()).getParent();
    }

    private static void run(String firmwareFilename) {
        ShellTask shellTask = new ShellTask(
                getWorkingDir(),
                getExec(firmwareFilename),
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
        String firmwareFilename = Axoloti.getFirmwareFilename();
        Thread thread = new Thread(() -> run(firmwareFilename));
        thread.start();
    }

    private static void println(String s) {
        Logger.getLogger(UploadFirmwareDFU.class.getName()).log(Level.INFO, s);
    }

}
