
package axoloti.swingui.patch.object.inlet;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.swingui.patch.object.AxoObjectInstanceViewAbstract;

/**
 *
 * @author jtaelman
 */
public class InletInstanceViewFactory {

    public static InletInstanceView createView(InletInstance inletInstance, IAxoObjectInstanceView obj) {
        InletInstanceView view = new InletInstanceView(inletInstance, (AxoObjectInstanceViewAbstract) obj);
        inletInstance.getController().addView(view);
        return view;
    }
}
