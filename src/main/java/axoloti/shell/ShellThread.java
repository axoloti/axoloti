package axoloti.shell;

import axoloti.job.JobProcessor;


/**
 *
 * @author jtaelman
 */
public class ShellThread {

    private ShellThread() {
    }

    private static ShellThread singleton;

    public static ShellThread getSingleton() {
        if (singleton == null) {
            singleton = new ShellThread();
        }

        return singleton;
    }

    private final JobProcessor jobProcessor = new JobProcessor();

    /*
    public boolean exec(ShellTask shellTask) {
        jobProcessor.exec(shellTask);
        try {
            jobProcessor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException ex) {
            throw new IllegalStateException(ex);
        }
        return shellTask.isSuccess();
    }
     */

}
