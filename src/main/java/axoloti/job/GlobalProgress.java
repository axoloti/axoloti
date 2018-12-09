package axoloti.job;

/**
 *
 * @author jtaelman
 */
public class GlobalProgress {

    private GlobalProgress() {
    }

    static private IProgressReporter instance;

    public static IProgressReporter getInstance() {
        return instance;
    }

    public static void setInstance(IProgressReporter instance) {
        GlobalProgress.instance = instance;
    }

}
