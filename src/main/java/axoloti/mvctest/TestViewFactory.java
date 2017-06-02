package axoloti.mvctest;

import axoloti.attribute.AttributeInstanceController;
import axoloti.attributeviews.AttributeInstanceViewFactory;
import axoloti.inlets.InletInstanceController;
import axoloti.inlets.InletInstanceViewFactory;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractView;
import axoloti.outlets.OutletInstanceController;
import axoloti.outlets.OutletInstanceViewFactory;
import axoloti.parameters.ParameterInstanceController;
import axoloti.parameterviews.ParameterInstanceViewFactory;

/**
 *
 * @author jtaelman
 */
public class TestViewFactory {

    public static AbstractView createView(AbstractController controller) {
        if (controller instanceof ParameterInstanceController) {
            return ParameterInstanceViewFactory.createView((ParameterInstanceController) controller, null);
        } else if (controller instanceof AttributeInstanceController) {
            return AttributeInstanceViewFactory.createView((AttributeInstanceController) controller, null);
        } else if (controller instanceof InletInstanceController) {
            return InletInstanceViewFactory.createView((InletInstanceController) controller, null);
        } else if (controller instanceof OutletInstanceController) {
            return OutletInstanceViewFactory.createView((OutletInstanceController) controller, null);
        }
        return null;
    }
}
