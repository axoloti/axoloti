
package axoloti.patch.object;

import axoloti.mvc.AbstractDocumentRoot;
import axoloti.patch.PatchController;

/**
 *
 * @author jtaelman
 */
public class ObjectInstancePatcherController extends ObjectInstanceController {
    
    PatchController subPatchController;

    @Override
    public AxoObjectInstancePatcher getModel() {
        return (AxoObjectInstancePatcher)super.getModel(); 
    }
    
    public ObjectInstancePatcherController(AxoObjectInstancePatcher model, AbstractDocumentRoot documentRoot, PatchController parent) {

        super(model, documentRoot, parent);
        model.getSubPatchModel().setContainer(model);
        subPatchController = new PatchController(model.getSubPatchModel(), documentRoot, this);
    }

    public PatchController getSubPatchController() {
        return subPatchController;
    }

}
