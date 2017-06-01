package axoloti.mvctest;

import axoloti.attribute.AttributeInstanceController;
import axoloti.attributeviews.AttributeInstanceView;
import axoloti.inlets.InletInstanceController;
import axoloti.inlets.InletInstanceView;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractView;
import axoloti.outlets.OutletInstanceController;
import axoloti.outlets.OutletInstanceView;
import axoloti.parameters.ParameterInstanceController;
import axoloti.parameterviews.ParameterInstanceView;

/**
 *
 * @author jtaelman
 */
public class TestViewFactory {

    public static AbstractView createView(AbstractController controller) {
        if (controller instanceof ParameterInstanceController) {
            return ParameterInstanceView.createView((ParameterInstanceController) controller, null);
        } else if (controller instanceof AttributeInstanceController) {
            return AttributeInstanceView.createView((AttributeInstanceController) controller, null);
        } else if (controller instanceof InletInstanceController) {
            return InletInstanceView.createView((InletInstanceController) controller, null);
        } else if (controller instanceof OutletInstanceController) {
            return OutletInstanceView.createView((OutletInstanceController) controller, null);
        }
        return null;
    }
}
