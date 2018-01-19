package axoloti.piccolo.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.datatypes.Value;
import axoloti.patch.object.parameter.ParameterInstanceController;
import axoloti.piccolo.components.control.PCheckboxComponent;

public class PParameterInstanceViewBin32 extends PParameterInstanceViewInt32 {

    public PParameterInstanceViewBin32(ParameterInstanceController controller,
            IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public PCheckboxComponent CreateControl() {
        return new PCheckboxComponent(0, 32, axoObjectInstanceView);
    }

    @Override
    public PCheckboxComponent getControlComponent() {
        return (PCheckboxComponent) ctrl;
    }
}
