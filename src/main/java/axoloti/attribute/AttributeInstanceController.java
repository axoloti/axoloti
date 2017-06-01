package axoloti.attribute;

import axoloti.attributeviews.IAttributeInstanceView;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;

/**
 *
 * @author jtaelman
 */
public class AttributeInstanceController extends AbstractController<AttributeInstance, IAttributeInstanceView> {

    public static final String ELEMENT_ATTR_VALUE = "Value";

    public AttributeInstanceController(AttributeInstance model, AbstractDocumentRoot documentRoot) {
        super(model, documentRoot);
    }

    public void changeValue(Object value) {
        setModelUndoableProperty(ELEMENT_ATTR_VALUE, value);
    }

}
