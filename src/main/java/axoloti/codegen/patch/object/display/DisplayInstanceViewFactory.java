package axoloti.codegen.patch.object.display;

import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.display.DisplayInstanceController;
import axoloti.patch.object.display.DisplayInstanceFrac32;
import axoloti.patch.object.display.DisplayInstanceFrac8S128VBar;
import axoloti.patch.object.display.DisplayInstanceFrac8U128VBar;
import axoloti.patch.object.display.DisplayInstanceInt32;

/**
 *
 * @author jtaelman
 */
public class DisplayInstanceViewFactory {

    public static DisplayInstanceView createView(DisplayInstanceController controller) {
        DisplayInstance model = controller.getModel();
        DisplayInstanceView view;
        if (model instanceof DisplayInstanceFrac32) {
            view = new DisplayInstanceViewFrac32(controller);
        } else if (model instanceof DisplayInstanceInt32) {
            view = new DisplayInstanceViewInt32(controller);
        } else if (model instanceof DisplayInstanceFrac8S128VBar) {
            view = new DisplayInstanceView128B(controller);
        } else if (model instanceof DisplayInstanceFrac8U128VBar) {
            view = new DisplayInstanceView128B(controller);
        } else {
            view = null;
            throw new Error("display type not implemented: " + model.getClass());
        }
        return view;
    }
}
