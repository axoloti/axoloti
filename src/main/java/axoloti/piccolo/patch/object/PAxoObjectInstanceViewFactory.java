package axoloti.piccolo.patch.object;

import axoloti.patch.object.AxoObjectInstance;
import axoloti.patch.object.AxoObjectInstanceComment;
import axoloti.patch.object.AxoObjectInstanceHyperlink;
import axoloti.patch.object.AxoObjectInstancePatcher;
import axoloti.patch.object.AxoObjectInstancePatcherObject;
import axoloti.patch.object.AxoObjectInstanceZombie;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.ObjectInstanceController;
import axoloti.piccolo.patch.PatchViewPiccolo;

public class PAxoObjectInstanceViewFactory {

    private PAxoObjectInstanceViewFactory() {
    }

    public static PAxoObjectInstanceViewAbstract createView(IAxoObjectInstance model, PatchViewPiccolo pvp) {
        ObjectInstanceController controller = model.getController();
        PAxoObjectInstanceViewAbstract view = null;
        if (model instanceof AxoObjectInstanceComment) {
            view = new PAxoObjectInstanceViewComment(model, pvp);
        } else if (model instanceof AxoObjectInstanceHyperlink) {
            view = new PAxoObjectInstanceViewHyperlink(model, pvp);
        } else if (model instanceof AxoObjectInstanceZombie) {
            view = new PAxoObjectInstanceViewZombie(model, pvp);
        } else if (model instanceof AxoObjectInstancePatcherObject) {
            view = new PAxoObjectInstanceViewPatcherObject(model, pvp);
        } else if (model instanceof AxoObjectInstancePatcher) {
            view = new PAxoObjectInstanceViewPatcher(model, pvp);
        } else if (model instanceof AxoObjectInstance) {
            view = new PAxoObjectInstanceView(model, pvp);
        } else {
            throw new Error("unknown object type");
        }
        controller.addView(view);
        return view;
    }
}
