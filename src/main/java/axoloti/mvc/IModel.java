package axoloti.mvc;

import axoloti.property.Property;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 *
 * @author jtaelman
 */
public interface IModel {

    IModel getParent();

    AbstractDocumentRoot getDocumentRoot();

    void setDocumentRoot(AbstractDocumentRoot documentRoot);

    List<Property> getProperties();

    void addPropertyChangeListener(PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);

    void firePropertyChange(Property property, Object oldValue, Object newValue);

    AbstractController getController();

}
