package axoloti.parameterviews;

import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceController;
import components.control.CheckboxComponent;

class ParameterInstanceViewBin32 extends ParameterInstanceViewInt32 {

    public ParameterInstanceViewBin32(ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public CheckboxComponent CreateControl() {
        return new CheckboxComponent(0, 32);
    }

    @Override
    public CheckboxComponent getControlComponent() {
        return (CheckboxComponent) ctrl;
    }
}
