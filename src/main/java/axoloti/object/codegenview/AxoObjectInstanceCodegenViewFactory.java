package axoloti.object.codegenview;

import axoloti.object.AxoObjectInstance;
import axoloti.object.AxoObjectInstanceComment;
import axoloti.object.AxoObjectInstanceHyperlink;
import axoloti.object.AxoObjectInstancePatcher;
import axoloti.object.IAxoObjectInstance;
import axoloti.object.ObjectInstanceController;
import axoloti.object.ObjectInstancePatcherController;

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
