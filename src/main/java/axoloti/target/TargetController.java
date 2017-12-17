package axoloti.target;

import static axoloti.target.TargetModel.getTargetModel;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.IView;

/**
 *
 * @author jtaelman
 */
public class TargetController extends AbstractController<TargetModel, IView, AbstractController> {

    public TargetController(TargetModel model, AbstractDocumentRoot documentRoot, AbstractController parent) {
        super(model, documentRoot, parent);
    }

    private static TargetController targetController;

    public static TargetController getTargetController() {
        if (targetController == null) {
            targetController = new TargetController(getTargetModel(), null, null);
        }
        return targetController;
    }

}
