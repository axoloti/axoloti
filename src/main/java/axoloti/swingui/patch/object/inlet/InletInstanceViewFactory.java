
package axoloti.swingui.patch.object.inlet;

import axoloti.patch.object.iolet.IoletInstanceController;
import axoloti.swingui.patch.object.AxoObjectInstanceViewAbstract;
import axoloti.abstractui.IAxoObjectInstanceView;

/**
 *
 * @author jtaelman
 */
public class InletInstanceViewFactory {

    public static InletInstanceView createView(IoletInstanceController controller, IAxoObjectInstanceView obj) {
        InletInstanceView view = new InletInstanceView(controller, (AxoObjectInstanceViewAbstract) obj);
        view.PostConstructor();
        controller.addView(view);
        return view;
    }
}
