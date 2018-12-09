package axoloti.job;

/**
 *
 * @author jtaelman
 */
public interface IJobContext {

    void setMaximum(int maximum);

    void setProgress(int nv);

    void setNote(String note);

    void setReady();

    boolean isCanceled();

    void reportException(Exception c);

    void doInSync(Runnable r);

    IJobContext[] createSubContexts(int number_of_sub_jobs);
    IJobContext[] createSubContexts(int[] ratios);
}
