package axoloti.mvc;

import java.beans.PropertyChangeEvent;

/**
 *
 * @author jtaelman
 */
public interface AbstractView<T extends AbstractController> {

    abstract void modelPropertyChange(PropertyChangeEvent evt);

    public abstract T getController();
}
