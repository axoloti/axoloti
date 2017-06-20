package axoloti.outlets;

import axoloti.atom.AtomDefinitionController;
import axoloti.object.AxoObjectInstance;

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
