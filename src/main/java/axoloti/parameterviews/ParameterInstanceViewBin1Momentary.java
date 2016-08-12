package axoloti.parameterviews;

import axoloti.datatypes.Value;
import axoloti.parameters.ParameterInstanceBin1Momentary;
import components.control.PulseButtonComponent;

public class ParameterInstanceViewBin1Momentary extends ParameterInstanceViewInt32 {

    public ParameterInstanceViewBin1Momentary(ParameterInstanceBin1Momentary parameterInstance) {
        super(parameterInstance);
    }
    
    @Override
    public void updateV() {
        ctrl.setValue(parameterInstance.getValue().getInt());
    }

    @Override
    public void setValue(Value value) {
        parameterInstance.setValue(value);
        updateV();
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