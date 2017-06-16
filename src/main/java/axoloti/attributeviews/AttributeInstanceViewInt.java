package axoloti.attributeviews;

import axoloti.attribute.AttributeInstanceController;
import axoloti.attribute.AttributeInstanceInt;
import axoloti.objectviews.IAxoObjectInstanceView;

abstract class AttributeInstanceViewInt extends AttributeInstanceView {

    AttributeInstanceViewInt(AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }
}
