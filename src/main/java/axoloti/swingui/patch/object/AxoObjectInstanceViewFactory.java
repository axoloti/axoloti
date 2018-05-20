package axoloti.swingui.patch.object;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.abstractui.IAxoObjectInstanceViewFactory;
import axoloti.abstractui.PatchView;
import axoloti.patch.object.AxoObjectInstance;
import axoloti.patch.object.AxoObjectInstanceComment;
import axoloti.patch.object.AxoObjectInstanceHyperlink;
import axoloti.patch.object.AxoObjectInstancePatcher;
import axoloti.patch.object.AxoObjectInstancePatcherObject;
import axoloti.patch.object.AxoObjectInstanceZombie;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.ObjectInstanceController;
import axoloti.swingui.patch.PatchViewSwing;

/**
 *
 * @author jtaelman
 */
public class AxoObjectInstanceViewFactory implements IAxoObjectInstanceViewFactory {

    protected AxoObjectInstanceViewFactory() {
    }

    private static AxoObjectInstanceViewFactory instance;

    public static AxoObjectInstanceViewFactory getInstance() {
        if(instance == null) {
            instance = new AxoObjectInstanceViewFactory();
        }
        return instance;
    }

    @Override
    public IAxoObjectInstanceView createView(IAxoObjectInstance model, PatchView patchView) {
        ObjectInstanceController controller = model.getController();
        AxoObjectInstanceViewAbstract view = null;
        PatchViewSwing patchViewSwing = (PatchViewSwing) patchView;
        if (model instanceof AxoObjectInstanceComment) {
            view = new AxoObjectInstanceViewComment((AxoObjectInstanceComment) model, patchViewSwing);
        } else if (model instanceof AxoObjectInstanceHyperlink) {
            view = new AxoObjectInstanceViewHyperlink((AxoObjectInstanceHyperlink) model, patchViewSwing);
        } else if (model instanceof AxoObjectInstanceZombie) {
            view = new AxoObjectInstanceViewZombie((AxoObjectInstanceZombie) model, patchViewSwing);
        } else if (model instanceof AxoObjectInstancePatcherObject) {
            view = new AxoObjectInstanceViewPatcherObject((AxoObjectInstancePatcherObject) model, patchViewSwing);
        } else if (model instanceof AxoObjectInstancePatcher) {
            view = new AxoObjectInstanceViewPatcher((AxoObjectInstancePatcher) model, patchViewSwing);
        } else if (model instanceof AxoObjectInstance) {
            view = new AxoObjectInstanceView(model, patchViewSwing);
        } else {
            throw new Error("unknown object type");
        }
        controller.addView(view);
        return view;
    }
}
