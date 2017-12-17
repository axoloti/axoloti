package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstanceController;
import axoloti.swingui.components.control.CheckboxComponent;

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
