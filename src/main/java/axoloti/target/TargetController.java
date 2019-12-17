package axoloti.target;

import axoloti.mvc.AbstractController;
import axoloti.mvc.IView;

/**
 *
 * @author jtaelman
 */
public class TargetController extends AbstractController<TargetModel, IView> {

    public TargetController(TargetModel model) {
        super(model);
    }

}
