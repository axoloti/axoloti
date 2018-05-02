package axoloti.codegen.patch.object;

import axoloti.patch.object.AxoObjectInstance;
import axoloti.patch.object.AxoObjectInstanceComment;
import axoloti.patch.object.AxoObjectInstanceHyperlink;
import axoloti.patch.object.AxoObjectInstancePatcher;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.ObjectInstanceController;
import axoloti.patch.object.ObjectInstancePatcherController;

/**
 *
 * @author jtaelman
 */
public class AxoObjectInstanceCodegenViewFactory {

    public static IAxoObjectInstanceCodegenView createView(ObjectInstanceController controller) {
        IAxoObjectInstance model = controller.getModel();
        IAxoObjectInstanceCodegenView view;
        if (model instanceof AxoObjectInstancePatcher) {
            view = new AxoObjectInstancePatcherCodegenView((ObjectInstancePatcherController) controller);
        } else if (model instanceof AxoObjectInstance) {
            view = new AxoObjectInstanceCodegenView(controller);
        } else if (model instanceof AxoObjectInstanceComment) {
            view = new AxoObjectInstanceDummyCodegenView(controller);
        } else if (model instanceof AxoObjectInstanceHyperlink) {
            view = new AxoObjectInstanceDummyCodegenView(controller);
        } else {
            return null;
        }
        controller.addView(view);
        return view;
    }
}
