package axoloti.swingui.patch.object.outlet;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.abstractui.IIoletInstanceView;
import axoloti.patch.object.iolet.IoletInstanceController;
import axoloti.swingui.patch.object.AxoObjectInstanceViewAbstract;

/**
 *
 * @author jtaelman
 */
public class OutletInstanceViewFactory {

    public static IIoletInstanceView createView(IoletInstanceController controller, IAxoObjectInstanceView obj) {
        OutletInstanceView view = new OutletInstanceView(controller, (AxoObjectInstanceViewAbstract) obj);
        view.PostConstructor();
        controller.addView(view);
        return view;
    }

}
