
package axoloti.inlets;

import axoloti.objectviews.AxoObjectInstanceViewAbstract;
import axoloti.objectviews.IAxoObjectInstanceView;

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
