package axoloti.piccolo.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.piccolo.components.control.PCheckboxComponent;

class PParameterInstanceViewBin16 extends PParameterInstanceViewBin {

    PParameterInstanceViewBin16(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public PCheckboxComponent createControl() {
        return new PCheckboxComponent(0, 16, axoObjectInstanceView);
    }

    @Override
    public PCheckboxComponent getControlComponent() {
        return (PCheckboxComponent) ctrl;
    }
}
