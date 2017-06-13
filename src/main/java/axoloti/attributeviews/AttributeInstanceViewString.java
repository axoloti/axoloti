package axoloti.attributeviews;

import axoloti.attribute.AttributeInstanceController;
import axoloti.attribute.AttributeInstanceString;
import axoloti.objectviews.IAxoObjectInstanceView;
import java.beans.PropertyChangeEvent;

abstract class AttributeInstanceViewString extends AttributeInstanceView {

    public AttributeInstanceViewString(AttributeInstanceString attributeInstance, AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attributeInstance, controller, axoObjectInstanceView);
    }

    public abstract void setString(String s);

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (evt.getPropertyName().equals(
                AttributeInstanceController.ELEMENT_ATTR_VALUE)) {
            String newValue = (String) evt.getNewValue();
            setString(newValue);
        }
    }
}
