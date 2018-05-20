package axoloti.mvc;

import java.beans.PropertyChangeEvent;

/**
 *
 * @author jtaelman
 */
public interface IView<M extends IModel> {

    void modelPropertyChange(PropertyChangeEvent evt);

    M getDModel();

    void dispose();
}
