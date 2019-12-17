package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.swingui.components.control.NumberBoxComponent;

class ParameterInstanceViewInt32BoxSmall extends ParameterInstanceViewInt32Box {

    ParameterInstanceViewInt32BoxSmall(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public NumberBoxComponent createControl() {
        return new NumberBoxComponent(0.0, getDModel().getMinValue(), getDModel().getMaxValue(), 1.0, 12, 12);
    }
}
