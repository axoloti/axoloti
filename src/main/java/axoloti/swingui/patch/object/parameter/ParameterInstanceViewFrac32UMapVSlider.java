package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.swingui.components.control.VSliderComponent;

class ParameterInstanceViewFrac32UMapVSlider extends ParameterInstanceViewFrac32U {

    ParameterInstanceViewFrac32UMapVSlider(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
        initCtrlComponent(ctrl);
    }

    @Override
    public VSliderComponent createControl() {
        return new VSliderComponent(0.0, 0.0, 64, 0.5);
    }

    private final VSliderComponent ctrl = createControl();

    @Override
    public VSliderComponent getControlComponent() {
        return ctrl;
    }
}
