package axoloti.attributeviews;

import axoloti.attribute.AttributeInstance;
import axoloti.attribute.AttributeInstanceController;
import axoloti.objectviews.IAxoObjectInstanceView;
import java.beans.PropertyChangeEvent;

abstract class AttributeInstanceViewString extends AttributeInstanceView {

    AttributeInstanceViewString(AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    public abstract void setString(String s);

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (evt.getPropertyName().equals(
                AttributeInstance.ELEMENT_ATTR_VALUE)) {
            String newValue = (String) evt.getNewValue();
            setString(newValue);
        }
    }
}
