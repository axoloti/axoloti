package axoloti.inlets;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.object.ObjectInstanceController;

/**
 *
 * @author jtaelman
 */
public class InletInstanceController extends AbstractController<InletInstance, IInletInstanceView, ObjectInstanceController> {

    public InletInstanceController(InletInstance model, AbstractDocumentRoot documentRoot, ObjectInstanceController parent) {
        super(model, documentRoot, parent);
    }

}
