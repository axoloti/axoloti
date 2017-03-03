package axoloti.piccolo.attributeviews;

import axoloti.attribute.AttributeInstanceInt;
import axoloti.objectviews.IAxoObjectInstanceView;

public abstract class PAttributeInstanceViewInt extends PAttributeInstanceView {

    AttributeInstanceInt attributeInstance;

    PAttributeInstanceViewInt(AttributeInstanceInt attributeInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attributeInstance, axoObjectInstanceView);
        this.attributeInstance = attributeInstance;

    }
}
