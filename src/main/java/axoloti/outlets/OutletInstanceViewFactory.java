package axoloti.outlets;

import axoloti.objectviews.AxoObjectInstanceViewAbstract;
import axoloti.objectviews.IAxoObjectInstanceView;

/**
 *
 * @author jtaelman
 */
public class OutletInstanceViewFactory {

    public static OutletInstanceView createView(OutletInstanceController controller, IAxoObjectInstanceView obj) {
        OutletInstance model = controller.getModel();
        OutletInstanceView view = new OutletInstanceView(model, controller, (AxoObjectInstanceViewAbstract) obj);
        view.PostConstructor();
        controller.addView(view);
        return view;
    }

}
