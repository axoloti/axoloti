package axoloti.patch.object.outlet;

import axoloti.object.outlet.Outlet;
import axoloti.patch.object.AxoObjectInstance;

/**
 *
 * @author jtaelman
 */
public class OutletInstanceFactory {

    private OutletInstanceFactory() {
    }

    static public OutletInstance createView(Outlet outlet, AxoObjectInstance axoObj) {
        OutletInstance i = new OutletInstance(outlet, axoObj);
        outlet.getController().addView(i);
        return i;
    }
}
