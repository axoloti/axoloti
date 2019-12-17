package axoloti.patch.object.attribute;

import axoloti.abstractui.IAttributeInstanceView;
import axoloti.mvc.AbstractController;

/**
 *
 * @author jtaelman
 */
public class AttributeInstanceController extends AbstractController<AttributeInstance, IAttributeInstanceView> {

    protected AttributeInstanceController(AttributeInstance model) {
        super(model);
    }

    public void changeValue(Object value) {
        setModelUndoableProperty(AttributeInstance.ATTR_VALUE, value);
    }

}
