package axoloti.outlets;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;

/**
 *
 * @author jtaelman
 */
public class OutletInstanceController extends AbstractController<OutletInstance, IOutletInstanceView> {

    public OutletInstanceController(OutletInstance model, AbstractDocumentRoot documentRoot) {
        super(model, documentRoot);
    }

}
