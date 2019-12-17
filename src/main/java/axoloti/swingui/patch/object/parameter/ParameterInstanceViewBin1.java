package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.swingui.components.control.CheckboxComponent;

class ParameterInstanceViewBin1 extends ParameterInstanceViewBin {

    ParameterInstanceViewBin1(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
        initCtrlComponent(ctrl);

    }

    @Override
    public CheckboxComponent createControl() {
        return new CheckboxComponent(0, 1);
    }

    private final CheckboxComponent ctrl = createControl();

    @Override
    public CheckboxComponent getControlComponent() {
        return ctrl;
    }

}
