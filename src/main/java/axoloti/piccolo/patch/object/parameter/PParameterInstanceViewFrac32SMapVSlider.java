package axoloti.piccolo.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstanceFrac32SMapVSlider;
import axoloti.piccolo.components.control.PVSliderComponent;

class PParameterInstanceViewFrac32SMapVSlider extends PParameterInstanceViewFrac32S {

    PParameterInstanceViewFrac32SMapVSlider(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public ParameterInstanceFrac32SMapVSlider getDModel() {
        return (ParameterInstanceFrac32SMapVSlider) super.getDModel();
    }


    @Override
    public PVSliderComponent createControl() {
        return new PVSliderComponent(0.0, getDModel().getMin(),
                getDModel().getMax(), getDModel().getTick(), axoObjectInstanceView);
    }

    @Override
    public PVSliderComponent getControlComponent() {
        return (PVSliderComponent) ctrl;
    }
}
