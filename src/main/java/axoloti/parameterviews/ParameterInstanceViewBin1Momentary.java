package axoloti.parameterviews;

import axoloti.datatypes.Value;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceBin1Momentary;
import axoloti.parameters.ParameterInstanceController;
import components.control.PulseButtonComponent;

class ParameterInstanceViewBin1Momentary extends ParameterInstanceViewBin {

    public ParameterInstanceViewBin1Momentary(ParameterInstanceBin1Momentary parameterInstance, ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, controller, axoObjectInstanceView);
    }

    @Override
    public void updateV() {
        ctrl.setValue(parameterInstance.getValue().getInt());
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
