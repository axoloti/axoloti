package axoloti.attributeviews;

import axoloti.attribute.AttributeInstanceController;
import axoloti.attribute.AttributeInstanceString;
import axoloti.objectviews.IAxoObjectInstanceView;

abstract class AttributeInstanceViewString extends AttributeInstanceView {

    public AttributeInstanceViewString(AttributeInstanceString attributeInstance, AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attributeInstance, controller, axoObjectInstanceView);
    }

    public abstract String getString();

    public abstract void setString(String s);
}
