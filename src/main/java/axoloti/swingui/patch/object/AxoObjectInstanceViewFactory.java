package axoloti.swingui.patch.object;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.abstractui.PatchView;
import axoloti.piccolo.patch.PatchViewPiccolo;
import axoloti.patch.object.AxoObjectInstance;
import axoloti.patch.object.AxoObjectInstanceComment;
import axoloti.patch.object.AxoObjectInstanceHyperlink;
import axoloti.patch.object.AxoObjectInstancePatcher;
import axoloti.patch.object.AxoObjectInstancePatcherObject;
import axoloti.patch.object.AxoObjectInstanceZombie;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.ObjectInstanceController;
import axoloti.piccolo.patch.object.PAxoObjectInstanceView;
import axoloti.piccolo.patch.object.PAxoObjectInstanceViewAbstract;
import axoloti.piccolo.patch.object.PAxoObjectInstanceViewComment;
import axoloti.piccolo.patch.object.PAxoObjectInstanceViewHyperlink;
import axoloti.piccolo.patch.object.PAxoObjectInstanceViewPatcher;
import axoloti.piccolo.patch.object.PAxoObjectInstanceViewPatcherObject;
import axoloti.piccolo.patch.object.PAxoObjectInstanceViewZombie;
import axoloti.swingui.patch.PatchViewSwing;

/**
 *
 * @author jtaelman
 */
public class AxoObjectInstanceViewFactory {

    public static IAxoObjectInstanceView createView(ObjectInstanceController controller, PatchView pv) {
	if(pv instanceof PatchViewSwing) {
	    return createView(controller, (PatchViewSwing) pv);
	}
	return createView(controller, (PatchViewPiccolo) pv);
    }

    public static IAxoObjectInstanceView createView(ObjectInstanceController controller, PatchViewSwing pv) {
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

    public static IAxoObjectInstanceView createView(ObjectInstanceController controller, PatchViewPiccolo pv) {
        IAxoObjectInstance model = controller.getModel();
        PAxoObjectInstanceViewAbstract view = null;
        if (model instanceof AxoObjectInstanceComment) {
            view = new PAxoObjectInstanceViewComment(controller, pv);
        } else if (model instanceof AxoObjectInstanceHyperlink) {
            view = new PAxoObjectInstanceViewHyperlink(controller, pv);
        } else if (model instanceof AxoObjectInstanceZombie) {
            view = new PAxoObjectInstanceViewZombie(controller, pv);
        } else if (model instanceof AxoObjectInstancePatcherObject) {
            view = new PAxoObjectInstanceViewPatcherObject(controller, pv);
        } else if (model instanceof AxoObjectInstancePatcher) {
            view = new PAxoObjectInstanceViewPatcher(controller, pv);
        } else if (model instanceof AxoObjectInstance) {
            view = new PAxoObjectInstanceView(controller, pv);
        } else {
            throw new Error("unknown object type");
        }
        controller.addView(view);
        view.PostConstructor();
        return view;
    }
}
