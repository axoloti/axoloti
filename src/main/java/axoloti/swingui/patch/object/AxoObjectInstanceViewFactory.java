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
    public IAxoObjectInstanceView createView(ObjectInstanceController controller, PatchView patchView) {
        IAxoObjectInstance model = controller.getModel();
        AxoObjectInstanceViewAbstract view = null;
        PatchViewSwing patchViewSwing = (PatchViewSwing) patchView;
        if (model instanceof AxoObjectInstanceComment) {
            view = new AxoObjectInstanceViewComment(controller, patchViewSwing);
        } else if (model instanceof AxoObjectInstanceHyperlink) {
            view = new AxoObjectInstanceViewHyperlink(controller, patchViewSwing);
        } else if (model instanceof AxoObjectInstanceZombie) {
            view = new AxoObjectInstanceViewZombie(controller, patchViewSwing);
        } else if (model instanceof AxoObjectInstancePatcherObject) {
            view = new AxoObjectInstanceViewPatcherObject(controller, patchViewSwing);
        } else if (model instanceof AxoObjectInstancePatcher) {
            view = new AxoObjectInstanceViewPatcher(controller, patchViewSwing);
        } else if (model instanceof AxoObjectInstance) {
            view = new AxoObjectInstanceView(controller, patchViewSwing);
        } else {
            throw new Error("unknown object type");
        }
        view.PostConstructor();
        controller.addView(view);
        return view;
    }
}
