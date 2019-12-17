package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.object.parameter.ParameterInt32;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstanceInt32Box;
import axoloti.swingui.components.control.NumberBoxComponent;
import java.beans.PropertyChangeEvent;

class ParameterInstanceViewInt32Box extends ParameterInstanceViewInt32 {

    ParameterInstanceViewInt32Box(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
        initCtrlComponent(ctrl);
    }

    @Override
    public ParameterInstanceInt32Box getDModel() {
        return (ParameterInstanceInt32Box) super.getDModel();
    }

    @Override
    public NumberBoxComponent createControl() {
        NumberBoxComponent n = new NumberBoxComponent(0.0, getDModel().getMinValue(), getDModel().getMaxValue(), 1.0);
        return n;
    }

    private final NumberBoxComponent ctrl = createControl();

    @Override
    public NumberBoxComponent getControlComponent() {
        return ctrl;
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
