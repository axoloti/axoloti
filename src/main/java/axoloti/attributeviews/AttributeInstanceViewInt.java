package axoloti.attributeviews;

import axoloti.attribute.AttributeInstanceController;
import axoloti.attribute.AttributeInstanceInt;
import axoloti.objectviews.IAxoObjectInstanceView;

abstract class AttributeInstanceViewInt extends AttributeInstanceView {

    AttributeInstanceViewInt(AttributeInstanceInt attributeInstance, AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attributeInstance, controller, axoObjectInstanceView);
    }
}
