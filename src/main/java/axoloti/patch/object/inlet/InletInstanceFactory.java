package axoloti.patch.object.inlet;

import axoloti.object.inlet.Inlet;
import axoloti.patch.object.AxoObjectInstance;

/**
 *
 * @author jtaelman
 */
public class InletInstanceFactory {

    private InletInstanceFactory() {
    }

    static public InletInstance createView(Inlet inlet, AxoObjectInstance axoObj) {
        InletInstance i = new InletInstance(inlet, axoObj);
        inlet.getController().addView(i);
        return i;
    }
}
