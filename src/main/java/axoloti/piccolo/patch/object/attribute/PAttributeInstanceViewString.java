package axoloti.piccolo.patch.object.attribute;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.attribute.AttributeInstanceString;
import java.beans.PropertyChangeEvent;

abstract class PAttributeInstanceViewString extends PAttributeInstanceView {

    PAttributeInstanceViewString(AttributeInstance attribute, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attribute, axoObjectInstanceView);
    }

    public abstract void setString(String s);

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (AttributeInstanceString.ATTR_VALUE.is(evt)) {
            String newValue = (String) evt.getNewValue();
            setString(newValue);
        }
    }
}
