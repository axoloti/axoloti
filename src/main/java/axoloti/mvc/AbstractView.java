package axoloti.mvc;

import java.beans.PropertyChangeEvent;

/**
 *
 * @author jtaelman
 */
public interface AbstractView {

    abstract void modelPropertyChange(PropertyChangeEvent evt);

    public abstract AbstractController getController();
}
