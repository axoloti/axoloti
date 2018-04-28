
package axoloti.swingui.patch.object.inlet;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.iolet.IoletInstanceController;
import axoloti.swingui.patch.object.AxoObjectInstanceViewAbstract;

/**
 *
 * @author jtaelman
 */
public class InletInstanceViewFactory {

    public static InletInstanceView createView(IoletInstanceController controller, IAxoObjectInstanceView obj) {
        InletInstanceView view = new InletInstanceView(controller, (AxoObjectInstanceViewAbstract) obj);
        controller.addView(view);
        return view;
    }
}
