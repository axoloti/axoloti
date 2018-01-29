package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.object.parameter.ParameterInt32;
import axoloti.object.parameter.ParameterInt32VRadio;
import axoloti.patch.object.parameter.ParameterInstanceController;
import axoloti.swingui.components.control.VRadioComponent;
import java.beans.PropertyChangeEvent;

class ParameterInstanceViewInt32VRadio extends ParameterInstanceViewInt32 {

    public ParameterInstanceViewInt32VRadio(ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public VRadioComponent CreateControl() {
        return new VRadioComponent(0, ((ParameterInt32VRadio) getModel().getModel()).getMaxValue());
    }

    @Override
    public VRadioComponent getControlComponent() {
        return (VRadioComponent) ctrl;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (ParameterInt32.VALUE_MAX.is(evt)) {
            getControlComponent().setMax((Integer) evt.getNewValue());
        }
    }
}
