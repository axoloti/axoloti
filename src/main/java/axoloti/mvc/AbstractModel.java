package axoloti.mvc;

import axoloti.property.Property;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author jtaelman
 */
public abstract class AbstractModel<C extends AbstractController> implements IModel {

    private C controllerOfModel = null;
    private AbstractDocumentRoot documentRoot;

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

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

    protected abstract C createController();

    @Override
    public C getController() {
        if (controllerOfModel == null) {
            controllerOfModel = createController();
        }
        return controllerOfModel;
    }

    @Override
    public AbstractDocumentRoot getDocumentRoot() {
        return documentRoot;
    }

    @Override
    public void setDocumentRoot(AbstractDocumentRoot documentRoot) {
        this.documentRoot = documentRoot;
    }

}
