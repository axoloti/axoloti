
package axoloti.patch.object;

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

    public ObjectInstancePatcherController(AxoObjectInstancePatcher model) {
        super(model);
        model.getSubPatchModel().setParent(model);
        subPatchController = model.getSubPatchModel().getControllerFromModel();
    }

    public PatchController getSubPatchController() {
        return subPatchController;
    }

}
