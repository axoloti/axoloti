package axoloti.mvc;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author jtaelman
 */
public abstract class AbstractModel {

    protected final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    final public void addPropertyChangeListener(PropertyChangeListener listener) {
//        System.out.println("addPropertyChangeListener " + this.toString() + " " + listener.toString() + " " + Arrays.toString(Thread.currentThread().getStackTrace()));
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    final public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    final protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
//        System.out.println("firePropertyChange " + propertyName + " " + newValue.toString() + " (" + propertyChangeSupport.getPropertyChangeListeners().length + ")" + this.toString());
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

}
