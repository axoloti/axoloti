package axoloti.attributeviews;

import axoloti.attribute.AttributeInstanceInt;
import axoloti.objectviews.AxoObjectInstanceView;

public abstract class AttributeInstanceViewInt extends AttributeInstanceView {

    AttributeInstanceInt attributeInstance;

    AttributeInstanceViewInt(AttributeInstanceInt attributeInstance, AxoObjectInstanceView axoObjectInstanceView) {
        super(attributeInstance, axoObjectInstanceView);
        this.attributeInstance = attributeInstance;

    }
}