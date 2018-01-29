package axoloti.swingui.patch.object.attribute;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.attribute.AttributeInstanceController;
import axoloti.patch.object.attribute.AttributeInstanceString;
import java.beans.PropertyChangeEvent;

abstract class AttributeInstanceViewString extends AttributeInstanceView {

    AttributeInstanceViewString(AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
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
