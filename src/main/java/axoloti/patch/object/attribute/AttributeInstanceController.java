package axoloti.patch.object.attribute;

import axoloti.abstractui.IAttributeInstanceView;
import axoloti.mvc.AbstractController;
import axoloti.patch.object.ObjectInstanceController;

/**
 *
 * @author jtaelman
 */
public class AttributeInstanceController extends AbstractController<AttributeInstance, IAttributeInstanceView, ObjectInstanceController> {

    protected AttributeInstanceController(AttributeInstance model) {
        super(model);
    }

    public void changeValue(Object value) {
        setModelUndoableProperty(AttributeInstance.ATTR_VALUE, value);
    }

}
