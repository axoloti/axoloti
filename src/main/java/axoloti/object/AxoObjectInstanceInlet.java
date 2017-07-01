
package axoloti.object;

import axoloti.PatchModel;
import axoloti.inlets.Inlet;
import axoloti.inlets.InletBool32;
import axoloti.inlets.InletFrac32;
import axoloti.inlets.InletFrac32Buffer;
import axoloti.inlets.InletInt32;
import java.awt.Point;
import java.util.ArrayList;


/**
 *
 * @author jtaelman
 */
public class AxoObjectInstanceInlet extends AxoObjectInstance {

    final Inlet parentInlet;
    
    public AxoObjectInstanceInlet(ObjectController controller, PatchModel patchModel, String InstanceName1, Point location) {
        super(controller, patchModel, InstanceName1, location);
        if (typeName.equals("patch/inlet a")) {
            parentInlet = new InletFrac32Buffer(getInstanceName(), "");
        } else if (typeName.equals("patch/inlet b")) {
            parentInlet = new InletBool32(getInstanceName(), "");
        } else if (typeName.equals("patch/inlet f")) {
            parentInlet = new InletFrac32(getInstanceName(), "");
        } else if (typeName.equals("patch/inlet i")) {
            parentInlet = new InletInt32(getInstanceName(), "");
        } else {
            parentInlet = null;            
            throw new Error("unkown inlet object type: " + typeName);
        }
        AxoObjectInstancePatcher aoip = getContainer();
        if (aoip == null) {
            return;
        }
        AxoObjectPatcher aop = (AxoObjectPatcher) aoip.getController().getModel();        
        ArrayList<Inlet> inlets = new ArrayList<>(aop.getInlets());
        inlets.add(parentInlet);
        aop.setInlets(inlets);
    }

    @Override
    public boolean setInstanceName(String s) {
        boolean r = super.setInstanceName(s);
        parentInlet.setName(s);
        return r;
    }

    @Override
    public void Remove() {
        AxoObjectInstancePatcher aoip = getContainer();
        if (aoip == null) {
            return;
        }
        AxoObjectPatcher aop = (AxoObjectPatcher) aoip.getController().getModel();        
        ArrayList<Inlet> inlets = new ArrayList<>(aop.getInlets());
        inlets.remove(parentInlet);
        aop.setInlets(inlets);
    }

}
