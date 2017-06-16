package axoloti.parameterviews;

import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceController;
import axoloti.parameters.ParameterInstanceFrac32U;

abstract class ParameterInstanceViewFrac32U extends ParameterInstanceViewFrac32 {

    ParameterInstanceViewFrac32U(ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }
}
