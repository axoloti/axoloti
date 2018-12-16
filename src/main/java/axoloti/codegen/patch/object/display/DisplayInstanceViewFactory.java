package axoloti.codegen.patch.object.display;

import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.display.DisplayInstanceFrac32;
import axoloti.patch.object.display.DisplayInstanceFrac8S128VBar;
import axoloti.patch.object.display.DisplayInstanceFrac8U128VBar;
import axoloti.patch.object.display.DisplayInstanceInt32;
import axoloti.patch.object.display.DisplayInstanceVScale;

/**
 *
 * @author jtaelman
 */
public class DisplayInstanceViewFactory {

    private DisplayInstanceViewFactory() {
    }

    public static DisplayInstanceView createView(DisplayInstance model) {
        DisplayInstanceView view;
        if (model instanceof DisplayInstanceFrac32) {
            view = new DisplayInstanceViewFrac32(model);
        } else if (model instanceof DisplayInstanceInt32) {
            view = new DisplayInstanceViewInt32(model);
        } else if (model instanceof DisplayInstanceFrac8S128VBar) {
            view = new DisplayInstanceView128B(model);
        } else if (model instanceof DisplayInstanceFrac8U128VBar) {
            view = new DisplayInstanceView128B(model);
        } else if (model instanceof DisplayInstanceVScale) {
            view = new DisplayInstanceView0(model);
        } else {
            view = null;
            throw new Error("display type not implemented: " + model.getClass());
        }
        return view;
    }
}
