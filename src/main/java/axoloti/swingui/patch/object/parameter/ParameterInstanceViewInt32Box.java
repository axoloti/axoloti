package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.object.parameter.ParameterInt32;
import axoloti.patch.object.parameter.ParameterInstanceController;
import axoloti.patch.object.parameter.ParameterInstanceInt32Box;
import axoloti.swingui.components.control.NumberBoxComponent;
import java.beans.PropertyChangeEvent;

class ParameterInstanceViewInt32Box extends ParameterInstanceViewInt32 {

    public ParameterInstanceViewInt32Box(ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public ParameterInstanceInt32Box getModel() {
        return (ParameterInstanceInt32Box) super.getModel();
    }

    @Override
    public NumberBoxComponent CreateControl() {
        NumberBoxComponent n = new NumberBoxComponent(0.0, getModel().getMinValue(), getModel().getMaxValue(), 1.0);
        return n;
    }

    @Override
    public NumberBoxComponent getControlComponent() {
        return (NumberBoxComponent) ctrl;
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
