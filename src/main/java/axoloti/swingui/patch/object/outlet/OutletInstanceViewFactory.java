package axoloti.swingui.patch.object.outlet;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.iolet.IoletInstanceController;
import axoloti.swingui.patch.object.AxoObjectInstanceViewAbstract;

/**
 *
 * @author jtaelman
 */
public class OutletInstanceViewFactory {

    public static OutletInstanceView createView(IoletInstanceController controller, IAxoObjectInstanceView obj) {
        OutletInstanceView view = new OutletInstanceView(controller, (AxoObjectInstanceViewAbstract) obj);
        controller.addView(view);
        return view;
    }

}
