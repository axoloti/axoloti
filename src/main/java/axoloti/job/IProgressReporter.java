package axoloti.job;

/**
 *
 * @author jtaelman
 */
public interface IProgressReporter {

    void setProgress(float value);

    void setProgressIndeterminate();

    void setNote(String note);

    void setReady();
}
