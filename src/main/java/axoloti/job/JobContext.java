package axoloti.job;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author jtaelman
 */
public class JobContext implements IJobContext {

    public JobContext() {
        this(0.0f, 1.0f);
    }

    public JobContext(float start, float end) {
        // System.out.println(String.format("new jobContext %f %f", start, end));
        this.start = start;
        this.end = end;
    }

    private final float start;
    private final float end;

    private int maximum = 100;

    @Override
    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    @Override
    public void setProgress(int nv) {
        GlobalProgress.getInstance().setProgress(start + (end - start) * (nv / (float) maximum));
    }

    @Override
    public void setReady() {
        GlobalProgress.getInstance().setReady();
    }

    @Override
    public boolean isCanceled() {
        return false;
        // GlobalProgress.instance().isCanceled();
    }

    @Override
    public void setNote(String note) {
        GlobalProgress.getInstance().setNote(note);
    }

    @Override
    public void reportException(Exception ex) {
        Logger.getLogger(JobContext.class.getName()).log(Level.SEVERE, null, ex);
    }

    @Override
    public void doInSync(Runnable r) {
        try {
            SwingUtilities.invokeAndWait(r);
        } catch (InterruptedException | InvocationTargetException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public IJobContext[] createSubContexts(int number_of_sub_jobs) {
        int[] n = new int[number_of_sub_jobs];
        for (int i = 0; i < number_of_sub_jobs; i++) {
            n[i] = 1;
        }
        return createSubContexts(n);
    }

    @Override
    public IJobContext[] createSubContexts(int[] ratios) {
        //System.out.println(String.format("creating %d subcontexts %f to %f", ratios.length, start, end));
        JobContext r[] = new JobContext[ratios.length];
        int sum = 0;
        for (int r1 : ratios) {
            sum += r1;
        }
        int cumsum = 0;
        for (int i = 0; i < ratios.length; i++) {
            r[i] = new JobContext(start + (end - start) * (cumsum / (float) sum), start + (end - start) * (cumsum + ratios[i]) / (float) sum);
            cumsum += ratios[i];
        }
        return r;
    }

}
