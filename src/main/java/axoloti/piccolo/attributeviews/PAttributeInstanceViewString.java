package axoloti.piccolo.attributeviews;

import axoloti.patch.object.attribute.AttributeInstanceString;
import axoloti.abstractui.IAxoObjectInstanceView;

public abstract class PAttributeInstanceViewString extends PAttributeInstanceView {

    AttributeInstanceString attributeInstance;

    public PAttributeInstanceViewString(AttributeInstanceString attributeInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attributeInstance, axoObjectInstanceView);
        this.attributeInstance = attributeInstance;

    }

    public abstract String getString();

    public abstract void setString(String s);
}
