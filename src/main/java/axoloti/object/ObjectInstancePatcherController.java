
package axoloti.object;

import axoloti.PatchController;
import axoloti.inlets.InletInstance;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.IView;
import axoloti.outlets.OutletInstance;
import axoloti.parameters.ParameterInstance;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

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
