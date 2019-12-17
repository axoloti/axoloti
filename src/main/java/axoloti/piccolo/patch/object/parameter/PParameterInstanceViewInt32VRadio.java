package axoloti.piccolo.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.object.parameter.ParameterInt32;
import axoloti.object.parameter.ParameterInt32VRadio;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.piccolo.components.PAssignMidiCCMenuItems;
import axoloti.piccolo.components.control.PVRadioComponent;
import java.beans.PropertyChangeEvent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

class PParameterInstanceViewInt32VRadio extends PParameterInstanceViewInt32 {

    PParameterInstanceViewInt32VRadio(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public PVRadioComponent createControl() {
        return new PVRadioComponent(0, ((ParameterInt32VRadio) getDModel().getDModel()).getMaxValue(), axoObjectInstanceView);
    }

    @Override
    public PVRadioComponent getControlComponent() {
        return (PVRadioComponent) ctrl;
    }

    @Override
    public void populatePopup(JPopupMenu m) {
        super.populatePopup(m);
        JMenu m1 = new JMenu("Midi CC");
        // AssignMidiCCMenuItems, does stuff in ctor
        PAssignMidiCCMenuItems assignMidiCCMenuItems = new PAssignMidiCCMenuItems(this, m1);
        m.add(m1);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (ParameterInt32.VALUE_MAX.is(evt)) {
            getControlComponent().setMax((Integer) evt.getNewValue());
        }
    }
}
