package axoloti.patch.object.display;

import axoloti.mvc.AbstractController;
import axoloti.mvc.IView;

/**
 *
 * @author jtaelman
 */
public class DisplayInstanceController extends AbstractController<DisplayInstance, IView> {

    protected DisplayInstanceController(DisplayInstance model) {
        super(model);
    }

    public void setValue(Object value) {
        setModelProperty(DisplayInstance.DISP_VALUE, value);
    }
}
