package axoloti.mvc;

import java.beans.PropertyChangeEvent;

/**
 *
 * @author jtaelman
 */
public interface IView<T extends AbstractController> {

    void modelPropertyChange(PropertyChangeEvent evt);

    T getController();

    void dispose();
}
