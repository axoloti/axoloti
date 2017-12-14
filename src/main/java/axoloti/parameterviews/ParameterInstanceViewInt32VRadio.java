package axoloti.parameterviews;

import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceController;
import axoloti.parameters.ParameterInt32;
import axoloti.parameters.ParameterInt32VRadio;
import components.control.VRadioComponent;
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
