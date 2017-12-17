package axoloti.swingui.patch.object.outlet;

import axoloti.swingui.patch.object.AxoObjectInstanceViewAbstract;
import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.outlet.OutletInstanceController;

/**
 *
 * @author jtaelman
 */
public class OutletInstanceViewFactory {

    public static OutletInstanceView createView(OutletInstanceController controller, IAxoObjectInstanceView obj) {
        OutletInstanceView view = new OutletInstanceView(controller, (AxoObjectInstanceViewAbstract) obj);
        view.PostConstructor();
        controller.addView(view);
        return view;
    }

}
