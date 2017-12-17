package axoloti.patch.object.outlet;

import axoloti.object.atom.AtomDefinitionController;
import axoloti.patch.object.AxoObjectInstance;

/**
 *
 * @author jtaelman
 */
public class OutletInstanceFactory {

    static public OutletInstance createView(AtomDefinitionController controller, AxoObjectInstance axoObj) {
        OutletInstance i = new OutletInstance(controller, axoObj);
        controller.addView(i);
        return i;
    }
}
