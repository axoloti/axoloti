package axoloti.parameterviews;

import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceController;
import axoloti.parameters.ParameterInstanceFrac32U;

public abstract class ParameterInstanceViewFrac32U extends ParameterInstanceViewFrac32 {

    ParameterInstanceViewFrac32U(ParameterInstanceFrac32U parameterInstance, ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, controller, axoObjectInstanceView);
    }
}
