package axoloti.attributeviews;

import axoloti.attribute.AttributeInstanceString;
import axoloti.objectviews.AxoObjectInstanceView;

public abstract class AttributeInstanceViewString extends AttributeInstanceView {

    AttributeInstanceString attributeInstance;

    public AttributeInstanceViewString(AttributeInstanceString attributeInstance, AxoObjectInstanceView axoObjectInstanceView) {
        super(attributeInstance, axoObjectInstanceView);
        this.attributeInstance = attributeInstance;

    }

    public abstract String getString();

    public abstract void setString(String s);
}
