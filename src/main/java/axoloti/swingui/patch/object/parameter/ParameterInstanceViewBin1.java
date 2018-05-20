package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.swingui.components.control.CheckboxComponent;

class ParameterInstanceViewBin1 extends ParameterInstanceViewBin {

    public ParameterInstanceViewBin1(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public CheckboxComponent createControl() {
        return new CheckboxComponent(0, 1);
    }

    @Override
    public void showPreset(int i) {
    }

    @Override
    public CheckboxComponent getControlComponent() {
        return (CheckboxComponent) ctrl;
    }

}
