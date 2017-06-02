package axoloti.parameterviews;

import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceController;
import axoloti.parameters.ParameterInstanceFrac32SMap;

class ParameterInstanceViewFrac32SMap extends ParameterInstanceViewFrac32UMap {

    public ParameterInstanceViewFrac32SMap(ParameterInstanceFrac32SMap parameterInstance, ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, controller, axoObjectInstanceView);
    }
}
