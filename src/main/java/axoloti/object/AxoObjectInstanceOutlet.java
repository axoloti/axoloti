package axoloti.object;

import axoloti.PatchModel;
import axoloti.outlets.Outlet;
import axoloti.outlets.OutletBool32;
import axoloti.outlets.OutletFrac32;
import axoloti.outlets.OutletFrac32Buffer;
import axoloti.outlets.OutletInt32;
import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author jtaelman
 */
public class AxoObjectInstanceOutlet extends AxoObjectInstance {

    final Outlet parentOutlet;

    public AxoObjectInstanceOutlet(ObjectController controller, PatchModel patchModel, String InstanceName1, Point location) {
        super(controller, patchModel, InstanceName1, location);

        if (typeName.equals("patch/outlet a")) {
            parentOutlet = new OutletFrac32Buffer(getInstanceName(), "");
        } else if (typeName.equals("patch/outlet b")) {
            parentOutlet = new OutletBool32(getInstanceName(), "");
        } else if (typeName.equals("patch/outlet f")) {
            parentOutlet = new OutletFrac32(getInstanceName(), "");
        } else if (typeName.equals("patch/outlet i")) {
            parentOutlet = new OutletInt32(getInstanceName(), "");
        } else {
            throw new Error("unkown outlet object type");
        }
        AxoObjectInstancePatcher aoip = getContainer();
        if (aoip == null) {
            return;
        }
        AxoObjectPatcher aop = (AxoObjectPatcher) aoip.getController().getModel();
        ArrayList<Outlet> outlets = new ArrayList<>(aop.getOutlets());
        outlets.add(parentOutlet);
        aop.setOutlets(outlets);
    }

    @Override
    public boolean setInstanceName(String s) {
        boolean r = super.setInstanceName(s);
        parentOutlet.setName(s);
        return r;
    }

    @Override
    public void Remove() {
        AxoObjectInstancePatcher aoip = getContainer();
        if (aoip == null) {
            return;
        }
        AxoObjectPatcher aop = (AxoObjectPatcher) aoip.getController().getModel();
        ArrayList<Outlet> outlets = new ArrayList<>(aop.getOutlets());
        outlets.remove(parentOutlet);
        aop.setOutlets(outlets);
    }

}
