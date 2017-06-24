
package axoloti.mvc;

import java.beans.PropertyChangeListener;

/**
 *
 * @author jtaelman
 */
public interface IModel {

    public void addPropertyChangeListener(PropertyChangeListener listener);

    public void removePropertyChangeListener(PropertyChangeListener listener);

    void firePropertyChange(String propertyName, Object oldValue, Object newValue);
}
