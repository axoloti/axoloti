package axoloti.patch.object.inlet;

import axoloti.object.atom.AtomDefinitionController;
import axoloti.patch.object.AxoObjectInstance;

/**
 *
 * @author jtaelman
 */
public class InletInstanceFactory {
    
    static public InletInstance createView(AtomDefinitionController controller, AxoObjectInstance axoObj) {
        InletInstance i = new InletInstance(controller, axoObj);
        controller.addView(i);
        return i;
    }
}
