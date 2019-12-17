package axoloti.piccolo.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.piccolo.components.control.PPulseButtonComponent;

class PParameterInstanceViewBin1Momentary extends PParameterInstanceViewBin {

    PParameterInstanceViewBin1Momentary(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public PPulseButtonComponent createControl() {
        return new PPulseButtonComponent(axoObjectInstanceView);
    }

    @Override
    public PPulseButtonComponent getControlComponent() {
        return (PPulseButtonComponent) ctrl;
    }
}
