package axoloti.piccolo.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.object.parameter.ParameterInt32;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstanceInt32Box;
import axoloti.piccolo.components.control.PNumberBoxComponent;
import java.beans.PropertyChangeEvent;

class PParameterInstanceViewInt32Box extends PParameterInstanceViewInt32 {

    PParameterInstanceViewInt32Box(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public ParameterInstanceInt32Box getDModel() {
        return (ParameterInstanceInt32Box) super.getDModel();
    }

    @Override
    public PNumberBoxComponent createControl() {
        PNumberBoxComponent n = new PNumberBoxComponent(
            0.0, getDModel().getMinValue(),
            getDModel().getMaxValue(), 1.0, axoObjectInstanceView);
        return n;
    }

    @Override
    public PNumberBoxComponent getControlComponent() {
        return (PNumberBoxComponent) ctrl;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (ParameterInt32.VALUE_MIN.is(evt)) {
            getControlComponent().setMin((Integer) evt.getNewValue());
        } else if (ParameterInt32.VALUE_MAX.is(evt)) {
            getControlComponent().setMax((Integer) evt.getNewValue());
        }
    }
}
