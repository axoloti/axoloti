package axoloti.parameterviews;

import axoloti.datatypes.Value;
import axoloti.parameters.ParameterInstanceBin16;
import components.control.CheckboxComponent;

public class ParameterInstanceViewBin16 extends ParameterInstanceViewInt32 {
    public ParameterInstanceViewBin16(ParameterInstanceBin16 parameterInstance) {
        super(parameterInstance);
    }
    
    @Override
    public CheckboxComponent CreateControl() {
        return new CheckboxComponent(0, 16);
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
    public CheckboxComponent getControlComponent() {
        return (CheckboxComponent) ctrl;
    }
}