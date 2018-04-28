package axoloti.mvc;

import java.beans.PropertyChangeEvent;

/**
 *
 * @author jtaelman
 */
public interface IView<T extends AbstractController> {

    abstract void modelPropertyChange(PropertyChangeEvent evt);

    abstract T getController();

    void dispose();
}
