package axoloti.piccolo.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.object.parameter.ParameterInt32;
import axoloti.object.parameter.ParameterInt32HRadio;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.piccolo.components.control.PHRadioComponent;
import java.beans.PropertyChangeEvent;

class PParameterInstanceViewInt32HRadio extends PParameterInstanceViewInt32 {

    PParameterInstanceViewInt32HRadio(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public PHRadioComponent createControl() {
        return new PHRadioComponent(0, ((ParameterInt32HRadio) getDModel().getDModel()).getMaxValue(), axoObjectInstanceView);
    }

    @Override
    public PHRadioComponent getControlComponent() {
        return (PHRadioComponent) ctrl;
    }

    // TODO: piccolo wtf?
    // @Override
    // public void populatePopup(JPopupMenu m) {
    //     super.populatePopup(m);
    //     JMenu m1 = new JMenu("Midi CC");
    //     new PAssignMidiCCMenuItems(this, m1);
    //     m.add(m1);
    // }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (ParameterInt32.VALUE_MAX.is(evt)) {
            getControlComponent().setMax((Integer) evt.getNewValue());
        }
    }
}
