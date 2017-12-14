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

    public AttributeInstanceController(AttributeInstance model, AbstractDocumentRoot documentRoot, ObjectInstanceController parent) {
        super(model, documentRoot, parent);
    }

}
