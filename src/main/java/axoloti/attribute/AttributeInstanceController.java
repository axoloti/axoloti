package axoloti.attribute;

import axoloti.attributeviews.IAttributeInstanceView;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.object.ObjectInstanceController;

/**
 *
 * @author jtaelman
 */
public class AttributeInstanceController extends AbstractController<AttributeInstance, IAttributeInstanceView, ObjectInstanceController> {

    public static final String ELEMENT_ATTR_VALUE = "Value";

    public AttributeInstanceController(AttributeInstance model, AbstractDocumentRoot documentRoot, ObjectInstanceController parent) {
        super(model, documentRoot, parent);
    }

    public void changeValue(Object value) {
        setModelUndoableProperty(ELEMENT_ATTR_VALUE, value);
    }

}
