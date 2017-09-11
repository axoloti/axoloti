package axoloti.mvc;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author jtaelman
 */
public abstract class AbstractModel implements IModel {

    protected final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public String[] getPropertyNames() {
        return new String[]{};
    }

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
    final public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
//        System.out.println("firePropertyChange " + propertyName + " " + newValue.toString() + " (" + propertyChangeSupport.getPropertyChangeListeners().length + ")" + this.toString());
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

}
