package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.swingui.components.control.PulseButtonComponent;

class ParameterInstanceViewBin1Momentary extends ParameterInstanceViewBin {

    public ParameterInstanceViewBin1Momentary(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public PulseButtonComponent createControl() {
        return new PulseButtonComponent();
    }

    @Override
    public PulseButtonComponent getControlComponent() {
        return (PulseButtonComponent) ctrl;
    }
}
