package axoloti.swingui.patch.object.outlet;

import axoloti.patch.object.outlet.OutletInstance;
import axoloti.swingui.patch.object.AxoObjectInstanceViewAbstract;

/**
 *
 * @author jtaelman
 */
public class OutletInstanceViewFactory {

    private OutletInstanceViewFactory() {
    }

    public static OutletInstanceView createView(OutletInstance outletInstance, AxoObjectInstanceViewAbstract obj) {
        OutletInstanceView view = new OutletInstanceView(outletInstance, obj);
        outletInstance.getController().addView(view);
        return view;
    }

}
