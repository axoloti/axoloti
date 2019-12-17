package axoloti.swingui.patch.object.attribute;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.attribute.AttributeInstanceString;
import java.beans.PropertyChangeEvent;

abstract class AttributeInstanceViewString extends AttributeInstanceView {

    AttributeInstanceViewString(AttributeInstance attribute, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attribute, axoObjectInstanceView);
    }

    abstract void setString(String s);

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (AttributeInstanceString.ATTR_VALUE.is(evt)) {
            String newValue = (String) evt.getNewValue();
            setString(newValue);
        }
    }
}
