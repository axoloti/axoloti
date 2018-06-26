package axoloti.piccolo.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.piccolo.components.control.PVSliderComponent;

class PParameterInstanceViewFrac32UMapVSlider extends PParameterInstanceViewFrac32U {

    PParameterInstanceViewFrac32UMapVSlider(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }


    @Override
    public PVSliderComponent createControl() {
        return new PVSliderComponent(0.0, 0.0, 64, 0.5, axoObjectInstanceView);
    }

    @Override
    public PVSliderComponent getControlComponent() {
        return (PVSliderComponent) ctrl;
    }
}
