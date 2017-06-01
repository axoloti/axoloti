package axoloti.parameterviews;

import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceController;
import axoloti.parameters.ParameterInstanceFrac32S;

public abstract class ParameterInstanceViewFrac32S extends ParameterInstanceViewFrac32 {

    ParameterInstanceViewFrac32S(ParameterInstanceFrac32S parameterInstance, ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, controller, axoObjectInstanceView);
    }
}
