package axoloti.object;

import axoloti.PatchController;
import axoloti.PatchViewObject;
import axoloti.mvc.AbstractDocumentRoot;

/**
 *
 * @author jtaelman
 */
public class ObjectInstancePatcherController extends ObjectInstanceController {

    public PatchController subPatchController;
    PatchViewObject pvo;

    public ObjectInstancePatcherController(AxoObjectInstancePatcher model, AbstractDocumentRoot documentRoot, PatchController parent) {
        super(model, documentRoot, parent);
        subPatchController = new PatchController(model.subPatchModel, documentRoot, parent);
        pvo = new PatchViewObject(subPatchController, (AxoObjectPatcher) getModel().getController().getModel());
        subPatchController.addView(pvo);
    }

}
