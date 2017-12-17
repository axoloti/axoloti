package axoloti.piccolo.parameterviews;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstanceInt32VRadio;
import axoloti.object.parameter.ParameterInt32VRadio;
import axoloti.piccolo.components.PAssignMidiCCMenuItems;
import axoloti.piccolo.components.control.PVRadioComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

public class PParameterInstanceViewInt32VRadio extends PParameterInstanceViewInt32 {

    public PParameterInstanceViewInt32VRadio(ParameterInstanceInt32VRadio parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public void updateV() {
        ctrl.setValue(getModel().getValue());
    }

    @Override
    public PVRadioComponent CreateControl() {
        return null;//new PVRadioComponent(0, ((ParameterInt32VRadio) parameterInstance.getModel()).MaxValue.getInt(), axoObjectInstanceView);
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
}
