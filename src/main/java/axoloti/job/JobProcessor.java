package axoloti.job;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jtaelman
 */
public class JobProcessor {

    private final ExecutorService e = java.util.concurrent.Executors.newSingleThreadExecutor();

    public void exec(IJob job) {
        e.execute(
                () -> {
                    IJobContext ctx = new JobContext();
                    try {
                        job.accept(ctx);
                        ctx.setReady();
                    } catch (Exception ex) {
                        Logger.getLogger(JobProcessor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
        );
    }

    public void awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        e.awaitTermination(timeout, unit);
    }

}
