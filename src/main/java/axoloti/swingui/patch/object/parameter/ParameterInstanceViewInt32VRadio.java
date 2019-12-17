package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.object.parameter.ParameterInt32;
import axoloti.object.parameter.ParameterInt32VRadio;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.swingui.components.control.VRadioComponent;
import java.beans.PropertyChangeEvent;

class ParameterInstanceViewInt32VRadio extends ParameterInstanceViewInt32 {

    ParameterInstanceViewInt32VRadio(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
        initCtrlComponent(ctrl);
    }

    @Override
    public VRadioComponent createControl() {
        return new VRadioComponent(0, ((ParameterInt32VRadio) getDModel().getDModel()).getMaxValue());
    }

    private final VRadioComponent ctrl = createControl();

    @Override
    public VRadioComponent getControlComponent() {
        return ctrl;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (ParameterInt32.VALUE_MAX.is(evt)) {
            getControlComponent().setMax((Integer) evt.getNewValue());
        }
    }
}
