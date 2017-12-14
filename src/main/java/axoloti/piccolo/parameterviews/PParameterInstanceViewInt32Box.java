package axoloti.piccolo.parameterviews;

import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceInt32Box;
import components.piccolo.control.PNumberBoxComponent;

public class PParameterInstanceViewInt32Box extends PParameterInstanceViewInt32 {

    public PParameterInstanceViewInt32Box(ParameterInstanceInt32Box parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public ParameterInstanceInt32Box getModel() {
        return (ParameterInstanceInt32Box) parameterInstance;
    }

    @Override
    public void updateV() {
        ctrl.setValue(getModel().getValue());
    }

    @Override
    public PNumberBoxComponent CreateControl() {
        PNumberBoxComponent n = new PNumberBoxComponent(0.0, getModel().getMinValue(),
                getModel().getMaxValue(), 1.0, axoObjectInstanceView);
        return n;
    }

    @Override
    public PNumberBoxComponent getControlComponent() {
        return (PNumberBoxComponent) ctrl;
    }
}
