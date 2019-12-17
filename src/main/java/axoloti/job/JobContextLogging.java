package axoloti.job;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author jtaelman
 */
public class JobContextLogging implements IJobContext {

    public JobContextLogging() {
        Logger.getLogger(JobContextLogging.class.getName()).log(Level.INFO, null, "new JobContextLogging");
    }

    @Override
    public void setMaximum(int maximum) {
    }

    @Override
    public void setProgress(int nv) {
    }

    @Override
    public void setReady() {
    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public void setNote(String note) {
        Logger.getLogger(JobContextLogging.class.getName()).log(Level.INFO, "job note : {0}", note);
    }

    @Override
    public void reportException(Exception ex) {
        Logger.getLogger(JobContextLogging.class.getName()).log(Level.SEVERE, null, ex);
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
    public IJobContext[] createSubContexts(int[] ratios) {
        return null;
    }

    @Override
    public IJobContext[] createSubContexts(int number_of_sub_jobs) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
