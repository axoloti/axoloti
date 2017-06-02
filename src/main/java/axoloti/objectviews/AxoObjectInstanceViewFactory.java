
package axoloti.objectviews;

import axoloti.PatchViewSwing;
import axoloti.object.AxoObjectInstance;
import axoloti.object.AxoObjectInstanceAbstract;
import axoloti.object.AxoObjectInstanceComment;
import axoloti.object.AxoObjectInstanceHyperlink;
import axoloti.object.AxoObjectInstancePatcher;
import axoloti.object.AxoObjectInstancePatcherObject;
import axoloti.object.AxoObjectInstanceZombie;
import axoloti.object.ObjectInstanceController;

/**
 *
 * @author jtaelman
 */
public class AxoObjectInstanceViewFactory {
    
    public static AxoObjectInstanceViewAbstract createView(ObjectInstanceController controller, PatchViewSwing pv) {
        AxoObjectInstanceAbstract model = controller.getModel();
        AxoObjectInstanceViewAbstract view = null;
        if (model instanceof AxoObjectInstanceComment) {
            view = new AxoObjectInstanceViewComment((AxoObjectInstanceComment)model, controller, pv);            
        } else if (model instanceof AxoObjectInstanceHyperlink) {
            view = new AxoObjectInstanceViewHyperlink((AxoObjectInstanceHyperlink)model, controller, pv);            
        } else if (model instanceof AxoObjectInstanceZombie) {
            view = new AxoObjectInstanceViewZombie((AxoObjectInstanceZombie)model, controller, pv);
        } else if (model instanceof AxoObjectInstancePatcherObject) {
            view = new AxoObjectInstanceViewPatcherObject((AxoObjectInstancePatcherObject)model, controller, pv);
        } else if (model instanceof AxoObjectInstancePatcher) {
            view = new AxoObjectInstanceViewPatcher((AxoObjectInstancePatcher)model, controller, pv);
        } else if (model instanceof AxoObjectInstance) {
            view = new AxoObjectInstanceView((AxoObjectInstance)model, controller, pv);
        }
        view.PostConstructor();
        controller.addView(view);
        return view;
    }    
}
