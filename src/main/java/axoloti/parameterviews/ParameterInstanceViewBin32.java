package axoloti.parameterviews;

import axoloti.datatypes.Value;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceBin32;
import axoloti.parameters.ParameterInstanceController;
import components.control.CheckboxComponent;

class ParameterInstanceViewBin32 extends ParameterInstanceViewInt32 {

    public ParameterInstanceViewBin32(ParameterInstanceBin32 parameterInstance, ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, controller, axoObjectInstanceView);
    }

    @Override
    public CheckboxComponent CreateControl() {
        return new CheckboxComponent(0, 32);
    }

    @Override
    public void updateV() {
        ctrl.setValue(parameterInstance.getValue().getInt());
    }

    @Override
    public CheckboxComponent getControlComponent() {
        return (CheckboxComponent) ctrl;
    }
}
