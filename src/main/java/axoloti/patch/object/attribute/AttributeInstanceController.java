package axoloti.patch.object.attribute;

import axoloti.abstractui.IAttributeInstanceView;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.patch.object.ObjectInstanceController;

/**
 *
 * @author jtaelman
 */
public class AttributeInstanceController extends AbstractController<AttributeInstance, IAttributeInstanceView, ObjectInstanceController> {

    public AttributeInstanceController(AttributeInstance model, AbstractDocumentRoot documentRoot, ObjectInstanceController parent) {
        super(model, documentRoot, parent);
    }

}
