package axoloti.codegen.patch.object;

import axoloti.patch.object.AxoObjectInstance;
import axoloti.patch.object.AxoObjectInstanceComment;
import axoloti.patch.object.AxoObjectInstanceHyperlink;
import axoloti.patch.object.AxoObjectInstancePatcher;
import axoloti.patch.object.IAxoObjectInstance;

/**
 *
 * @author jtaelman
 */
public class AxoObjectInstanceCodegenViewFactory {

    private AxoObjectInstanceCodegenViewFactory() {
    }

    public static IAxoObjectInstanceCodegenView createView(IAxoObjectInstance model) {
        IAxoObjectInstanceCodegenView view;
        if (model instanceof AxoObjectInstancePatcher) {
            view = new AxoObjectInstancePatcherCodegenView((AxoObjectInstancePatcher) model);
        } else if (model instanceof AxoObjectInstance) {
            view = new AxoObjectInstanceCodegenView(model);
        } else if (model instanceof AxoObjectInstanceComment) {
            view = new AxoObjectInstanceDummyCodegenView(model);
        } else if (model instanceof AxoObjectInstanceHyperlink) {
            view = new AxoObjectInstanceDummyCodegenView(model);
        } else {
            return null;
        }
        model.getController().addView(view);
        return view;
    }
}
