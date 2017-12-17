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
        IAxoObjectInstanceCodegenView view = null;
        if (model instanceof AxoObjectInstancePatcher) {
            view = new AxoObjectInstancePatcherCodegenView((AxoObjectInstancePatcher) model, (ObjectInstancePatcherController) controller);
        } else if (model instanceof AxoObjectInstance) {
            view = new AxoObjectInstanceCodegenView((AxoObjectInstance) model, controller);
        } else if (model instanceof AxoObjectInstanceComment) {
            view = new AxoObjectInstanceCommentCodegenView((AxoObjectInstanceComment) model, controller);
        } else if (model instanceof AxoObjectInstanceHyperlink) {
            view = new AxoObjectInstanceHyperlinkCodegenView((AxoObjectInstanceHyperlink) model, controller);
        } else {
            return null;
        }
        controller.addView(view);
        return view;
    }
}
