package axoloti.swingui.patch.object;

import axoloti.swingui.patch.PatchViewSwing;
import axoloti.patch.object.AxoObjectInstance;
import axoloti.patch.object.AxoObjectInstanceComment;
import axoloti.patch.object.AxoObjectInstanceHyperlink;
import axoloti.patch.object.AxoObjectInstancePatcher;
import axoloti.patch.object.AxoObjectInstancePatcherObject;
import axoloti.patch.object.AxoObjectInstanceZombie;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.ObjectInstanceController;

/**
 *
 * @author jtaelman
 */
public class AxoObjectInstanceViewFactory {

    public static AxoObjectInstanceViewAbstract createView(ObjectInstanceController controller, PatchViewSwing pv) {
        IAxoObjectInstance model = controller.getModel();
        AxoObjectInstanceViewAbstract view = null;
        if (model instanceof AxoObjectInstanceComment) {
            view = new AxoObjectInstanceViewComment(controller, pv);
        } else if (model instanceof AxoObjectInstanceHyperlink) {
            view = new AxoObjectInstanceViewHyperlink(controller, pv);
        } else if (model instanceof AxoObjectInstanceZombie) {
            view = new AxoObjectInstanceViewZombie(controller, pv);
        } else if (model instanceof AxoObjectInstancePatcherObject) {
            view = new AxoObjectInstanceViewPatcherObject(controller, pv);
        } else if (model instanceof AxoObjectInstancePatcher) {
            view = new AxoObjectInstanceViewPatcher(controller, pv);
        } else if (model instanceof AxoObjectInstance) {
            view = new AxoObjectInstanceView(controller, pv);
        } else {
            throw new Error("unknown object type");
        }
        view.PostConstructor();
        controller.addView(view);
        return view;
    }
}
