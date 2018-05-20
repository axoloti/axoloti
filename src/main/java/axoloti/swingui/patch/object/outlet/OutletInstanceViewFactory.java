package axoloti.swingui.patch.object.outlet;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.swingui.patch.object.AxoObjectInstanceViewAbstract;

/**
 *
 * @author jtaelman
 */
public class OutletInstanceViewFactory {

    public static OutletInstanceView createView(OutletInstance outletInstance, IAxoObjectInstanceView obj) {
        OutletInstanceView view = new OutletInstanceView(outletInstance, (AxoObjectInstanceViewAbstract) obj);
        outletInstance.getController().addView(view);
        return view;
    }

}
