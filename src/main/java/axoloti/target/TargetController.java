package axoloti.target;

import axoloti.mvc.AbstractController;
import axoloti.mvc.IView;
import static axoloti.target.TargetModel.getTargetModel;

/**
 *
 * @author jtaelman
 */
public class TargetController extends AbstractController<TargetModel, IView, AbstractController> {

    public TargetController(TargetModel model) {
        super(model);
    }

    private static TargetController targetController;

    public static TargetController getTargetController() {
        if (targetController == null) {
            targetController = new TargetController(getTargetModel());
        }
        return targetController;
    }

}
