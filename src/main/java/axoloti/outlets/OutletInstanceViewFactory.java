package axoloti.outlets;

import axoloti.objectviews.AxoObjectInstanceViewAbstract;
import axoloti.objectviews.IAxoObjectInstanceView;

/**
 *
 * @author jtaelman
 */
public class OutletInstanceViewFactory {

    public static OutletInstanceView createView(OutletInstanceController controller, IAxoObjectInstanceView obj) {
        OutletInstanceView view = new OutletInstanceView(controller, (AxoObjectInstanceViewAbstract) obj);
        view.PostConstructor();
        controller.addView(view);
        return view;
    }

}
