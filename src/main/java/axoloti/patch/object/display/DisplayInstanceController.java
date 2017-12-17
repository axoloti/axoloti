package axoloti.patch.object.display;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.IView;
import axoloti.patch.object.ObjectInstanceController;

/**
 *
 * @author jtaelman
 */
public class DisplayInstanceController extends AbstractController<DisplayInstance, IView, ObjectInstanceController> {

    public DisplayInstanceController(DisplayInstance model, AbstractDocumentRoot documentRoot, AbstractController parent) {
        super(model, documentRoot, (ObjectInstanceController) parent);
    }

    void setValue(Object value) {
        setModelProperty(DisplayInstance.DISP_VALUE, value);
    }
}
