package axoloti.inlets;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;

/**
 *
 * @author jtaelman
 */
public class InletInstanceController extends AbstractController<InletInstance, IInletInstanceView> {

    public InletInstanceController(InletInstance model, AbstractDocumentRoot documentRoot) {
        super(model, documentRoot);
    }

}
