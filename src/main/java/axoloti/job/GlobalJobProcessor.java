package axoloti.job;

/**
 *
 * @author jtaelman
 */
public class GlobalJobProcessor {

    private GlobalJobProcessor() {
    }

    private static final JobProcessor jobProcessor = new JobProcessor();

    public static JobProcessor getJobProcessor() {
        return jobProcessor;
    }
}
