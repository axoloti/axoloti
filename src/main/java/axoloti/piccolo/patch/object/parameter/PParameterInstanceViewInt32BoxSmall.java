package axoloti.piccolo.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.piccolo.components.control.PNumberBoxComponent;

class PParameterInstanceViewInt32BoxSmall extends PParameterInstanceViewInt32Box {

    PParameterInstanceViewInt32BoxSmall(ParameterInstance parameterInstance,
            IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public PNumberBoxComponent createControl() {
        return new PNumberBoxComponent(0.0, getDModel().getMinValue(),
                getDModel().getMaxValue(), 1.0, 12, 12, axoObjectInstanceView);
    }
}
