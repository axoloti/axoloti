package axoloti.patch.object.parameter;

import axoloti.object.atom.AtomDefinitionController;
import axoloti.object.parameter.Parameter;
import axoloti.patch.object.AxoObjectInstance;

/**
 *
 * @author jtaelman
 */
public class ParameterInstanceFactory {

    public static ParameterInstance createView(AtomDefinitionController controller, AxoObjectInstance obj) {
        Parameter model = (Parameter) controller.getModel();
        ParameterInstance view = model.CreateInstance((AxoObjectInstance) obj);
        view.setController(controller);
        controller.addView(view);
        return view;
    }
}
