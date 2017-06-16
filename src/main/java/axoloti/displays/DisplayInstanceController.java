package axoloti.displays;

import axoloti.displayviews.IDisplayInstanceView;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.object.ObjectInstanceController;

/**
 *
 * @author jtaelman
 */
public class DisplayInstanceController extends AbstractController<DisplayInstance, IDisplayInstanceView, ObjectInstanceController> {

    public static final String DISP_VALUE = "Value";

    public DisplayInstanceController(DisplayInstance model, AbstractDocumentRoot documentRoot, AbstractController parent) {
        super(model, documentRoot, (ObjectInstanceController) parent);
    }

    void setValue(Object value) {
        setModelProperty(DISP_VALUE, value);
    }
}
