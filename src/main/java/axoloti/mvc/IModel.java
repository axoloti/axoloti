
package axoloti.mvc;

import axoloti.property.Property;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 *
 * @author jtaelman
 */
public interface IModel {

    public List<Property> getProperties();

    public void addPropertyChangeListener(PropertyChangeListener listener);

    public void removePropertyChangeListener(PropertyChangeListener listener);

    void firePropertyChange(Property property, Object oldValue, Object newValue);
}
