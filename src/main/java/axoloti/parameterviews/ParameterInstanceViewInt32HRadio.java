package axoloti.parameterviews;

import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceController;
import axoloti.parameters.ParameterInt32;
import axoloti.parameters.ParameterInt32HRadio;
import components.control.HRadioComponent;
import java.beans.PropertyChangeEvent;

class ParameterInstanceViewInt32HRadio extends ParameterInstanceViewInt32 {

    public ParameterInstanceViewInt32HRadio(ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public HRadioComponent CreateControl() {
        return new HRadioComponent(0, ((ParameterInt32HRadio) getModel().getModel()).getMaxValue());
    }

    @Override
    public HRadioComponent getControlComponent() {
        return (HRadioComponent) ctrl;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (ParameterInt32.VALUE_MAX.is(evt)) {
            getControlComponent().setMax((Integer) evt.getNewValue());
        }
    }

}
