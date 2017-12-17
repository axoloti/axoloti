
package axoloti.swingui.patch.object.inlet;

import axoloti.patch.object.inlet.InletInstanceController;
import axoloti.swingui.patch.object.AxoObjectInstanceViewAbstract;
import axoloti.abstractui.IAxoObjectInstanceView;

/**
 *
 * @author jtaelman
 */
public class InletInstanceViewFactory {
    
    public static InletInstanceView createView(InletInstanceController controller, IAxoObjectInstanceView obj) {
        InletInstanceView view = new InletInstanceView(controller, (AxoObjectInstanceViewAbstract) obj);
        view.PostConstructor();
        controller.addView(view);
        return view;
    }
}
