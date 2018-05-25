package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.swingui.components.control.PulseButtonComponent;

class ParameterInstanceViewBin1Momentary extends ParameterInstanceViewBin {

    ParameterInstanceViewBin1Momentary(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
        initCtrlComponent(ctrl);
    }

    @Override
    public PulseButtonComponent createControl() {
        return new PulseButtonComponent();
    }

    private final PulseButtonComponent ctrl = createControl();

    @Override
    public PulseButtonComponent getControlComponent() {
        return ctrl;
    }
}
