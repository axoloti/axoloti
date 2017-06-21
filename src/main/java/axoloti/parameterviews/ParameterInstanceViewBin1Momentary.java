package axoloti.parameterviews;

import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceController;
import components.control.PulseButtonComponent;

class ParameterInstanceViewBin1Momentary extends ParameterInstanceViewBin {

    public ParameterInstanceViewBin1Momentary(ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public PulseButtonComponent CreateControl() {
        return new PulseButtonComponent();
    }

    @Override
    public PulseButtonComponent getControlComponent() {
        return (PulseButtonComponent) ctrl;
    }
}
