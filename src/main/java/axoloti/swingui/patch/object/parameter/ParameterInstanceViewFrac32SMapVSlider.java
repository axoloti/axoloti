package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstanceFrac32SMapVSlider;
import axoloti.swingui.components.control.VSliderComponent;

class ParameterInstanceViewFrac32SMapVSlider extends ParameterInstanceViewFrac32S {

    ParameterInstanceViewFrac32SMapVSlider(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
        initCtrlComponent(ctrl);
    }

    @Override
    public ParameterInstanceFrac32SMapVSlider getDModel() {
        return (ParameterInstanceFrac32SMapVSlider) super.getDModel();
    }

    @Override
    public VSliderComponent createControl() {
        return new VSliderComponent(0.0, getDModel().getMin(), getDModel().getMax(), getDModel().getTick());
    }

    private final VSliderComponent ctrl = createControl();

    @Override
    public VSliderComponent getControlComponent() {
        return ctrl;
    }
}
