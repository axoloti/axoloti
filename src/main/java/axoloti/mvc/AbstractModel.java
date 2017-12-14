package axoloti.mvc;

import axoloti.property.Property;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

/**
 *
 * @author jtaelman
 */
public abstract class AbstractModel implements IModel {

    protected final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    @Override
    public abstract List<Property> getProperties();

    @Override
    final public void addPropertyChangeListener(PropertyChangeListener listener) {
//        System.out.println("addPropertyChangeListener " + this.toString() + " " + listener.toString() + " " + Arrays.toString(Thread.currentThread().getStackTrace()));
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    final public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    final public void firePropertyChange(Property property, Object oldValue, Object newValue) {
//        System.out.println("firePropertyChange " + propertyName + " " + newValue.toString() + " (" + propertyChangeSupport.getPropertyChangeListeners().length + ")" + this.toString());
        propertyChangeSupport.firePropertyChange(property.getName(), oldValue, newValue);
    }

}
