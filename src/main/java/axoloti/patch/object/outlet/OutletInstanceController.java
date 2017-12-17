package axoloti.patch.object.outlet;

import axoloti.abstractui.IOutletInstanceView;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.patch.object.ObjectInstanceController;

/**
 *
 * @author jtaelman
 */
public class OutletInstanceController extends AbstractController<OutletInstance, IOutletInstanceView, ObjectInstanceController> {

    public OutletInstanceController(OutletInstance model, AbstractDocumentRoot documentRoot, ObjectInstanceController parent) {
        super(model, documentRoot, parent);
    }

}
