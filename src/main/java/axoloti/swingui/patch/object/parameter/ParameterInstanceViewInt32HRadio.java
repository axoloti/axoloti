package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.object.parameter.ParameterInt32;
import axoloti.object.parameter.ParameterInt32HRadio;
import axoloti.patch.object.parameter.ParameterInstanceController;
import axoloti.swingui.components.control.HRadioComponent;
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
