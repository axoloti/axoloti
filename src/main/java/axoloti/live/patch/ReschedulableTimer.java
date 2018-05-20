package axoloti.live.patch;

/**
 *
 * @author jtaelman
 */
import java.util.Timer;
import java.util.TimerTask;

public class ReschedulableTimer extends Timer {

    private Runnable task;
    private TimerTask timerTask = null;

    public void schedule(Runnable runnable, long delay) {
        task = runnable;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        };
        this.schedule(timerTask, delay);
    }

    public void reschedule(long delay) {
        timerTask.cancel();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        };
        this.schedule(timerTask, delay);
    }
}
