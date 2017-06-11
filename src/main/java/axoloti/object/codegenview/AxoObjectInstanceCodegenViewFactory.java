package axoloti.object.codegenview;

import axoloti.object.AxoObjectInstance;
import axoloti.object.AxoObjectInstanceAbstract;
import axoloti.object.AxoObjectInstanceComment;
import axoloti.object.AxoObjectInstanceHyperlink;
import axoloti.object.ObjectInstanceController;

/**
 *
 * @author jtaelman
 */
public class AxoObjectInstanceCodegenViewFactory {
    public static AxoObjectInstanceAbstractCodegenView createView(ObjectInstanceController controller) {
        AxoObjectInstanceAbstract model = controller.getModel();
        AxoObjectInstanceAbstractCodegenView view = null;
        if (model instanceof AxoObjectInstance) {
            view = new AxoObjectInstanceCodegenView((AxoObjectInstance)model, controller);
        } else if (model instanceof AxoObjectInstanceComment) {
            view = new AxoObjectInstanceCommentCodegenView((AxoObjectInstanceComment)model, controller);
        } else if (model instanceof AxoObjectInstanceHyperlink) {
            view = new AxoObjectInstanceHyperlinkCodegenView((AxoObjectInstanceHyperlink)model, controller);
        } else {
            return null;
        }
        controller.addView(view);
        return view;
    }
}
