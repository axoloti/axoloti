
package axoloti.swingui.patch.object.inlet;

import axoloti.patch.object.inlet.InletInstance;
import axoloti.swingui.patch.object.AxoObjectInstanceViewAbstract;

/**
 *
 * @author jtaelman
 */
public class InletInstanceViewFactory {

    private InletInstanceViewFactory() {
    }

    public static InletInstanceView createView(InletInstance inletInstance, AxoObjectInstanceViewAbstract obj) {
        InletInstanceView view = new InletInstanceView(inletInstance, obj);
        inletInstance.getController().addView(view);
        return view;
    }
}
