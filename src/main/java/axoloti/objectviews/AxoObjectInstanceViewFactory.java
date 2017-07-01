package axoloti.objectviews;

import axoloti.PatchViewSwing;
import axoloti.object.AxoObjectInstance;
import axoloti.object.AxoObjectInstanceComment;
import axoloti.object.AxoObjectInstanceHyperlink;
import axoloti.object.AxoObjectInstancePatcher;
import axoloti.object.AxoObjectInstancePatcherObject;
import axoloti.object.AxoObjectInstanceZombie;
import axoloti.object.IAxoObjectInstance;
import axoloti.object.ObjectInstanceController;

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
