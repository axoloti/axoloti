package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.swingui.components.control.CheckboxComponent;

class ParameterInstanceViewBin16 extends ParameterInstanceViewBin {

    public ParameterInstanceViewBin16(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public CheckboxComponent createControl() {
        return new CheckboxComponent(0, 16);
    }

    @Override
    public CheckboxComponent getControlComponent() {
        return (CheckboxComponent) ctrl;
    }
}
