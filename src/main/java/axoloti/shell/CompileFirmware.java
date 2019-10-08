package axoloti.shell;

import axoloti.job.GlobalJobProcessor;
import axoloti.target.TargetModel;
import axoloti.utils.OSDetect;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author jtaelman
 */
public class CompileFirmware {

    private CompileFirmware() {
    }

    private static String[] getExec() {
        if (OSDetect.getOS() == OSDetect.OS.WIN) {
            return new String[]{ShellTask.getFirmwareDir() + "/compile_firmware_win.bat"};
        } else if (OSDetect.getOS() == OSDetect.OS.MAC) {
            return new String[]{"/bin/sh", "./compile_firmware_osx.sh"};
        } else if (OSDetect.getOS() == OSDetect.OS.LINUX) {
            return new String[]{"/bin/sh", "./compile_firmware_linux.sh"};
        } else {
            Logger.getLogger(CompileFirmware.class.getName()).log(Level.SEVERE, "UPLOAD: OS UNKNOWN!");
            return null;
        }
    }

    private static String getWorkingDir() {
        return System.getProperty(axoloti.Axoloti.FIRMWARE_DIR);
    }

    private static void run() throws ExecutionFailedException {
        ShellTask shellTask = new ShellTask(
                getWorkingDir(),
                getExec(),
                ShellTask.getEnvironment());
        GlobalJobProcessor.getJobProcessor().exec(shellTask.getJob());
        boolean success = shellTask.isSuccess();
        if (success) {
            println("Done compiling firmware");
            try {
                SwingUtilities.invokeAndWait(() -> {
                    TargetModel.getTargetModel().updateLinkFirmwareID();
                    TargetModel.getTargetModel().setWarnedAboutFWCRCMismatch(false);
                });
            } catch (InterruptedException ex) {
                Logger.getLogger(CompileFirmware.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(CompileFirmware.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            println("Compiling firmware failed!");
            throw new ExecutionFailedException();
        }
    }

    public static void doit() {
        Thread thread = new Thread(() -> {
            try {
                run();
            } catch (ExecutionFailedException ex) {
                Logger.getLogger(CompileFirmware.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        thread.start();
    }

    private static void println(String s) {
        Logger.getLogger(UploadFirmwareDFU.class.getName()).log(Level.INFO, s);
    }

}
