package axoloti.patch.object.display;

import axoloti.mvc.AbstractController;
import axoloti.mvc.IView;
import axoloti.patch.object.ObjectInstanceController;

/**
 *
 * @author jtaelman
 */
public class DisplayInstanceController extends AbstractController<DisplayInstance, IView, ObjectInstanceController> {

    protected DisplayInstanceController(DisplayInstance model) {
        super(model);
    }

    void setValue(Object value) {
        setModelProperty(DisplayInstance.DISP_VALUE, value);
    }
}
