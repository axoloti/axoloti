package axoloti.piccolo.attributeviews;

import axoloti.patch.object.attribute.AttributeInstanceInt;
import axoloti.abstractui.IAxoObjectInstanceView;

public abstract class PAttributeInstanceViewInt extends PAttributeInstanceView {

    AttributeInstanceInt attributeInstance;

    PAttributeInstanceViewInt(AttributeInstanceInt attributeInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attributeInstance, axoObjectInstanceView);
        this.attributeInstance = attributeInstance;

    }
}
