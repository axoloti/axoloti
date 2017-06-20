package axoloti.inlets;

import axoloti.atom.AtomDefinitionController;
import axoloti.object.AxoObjectInstance;

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
