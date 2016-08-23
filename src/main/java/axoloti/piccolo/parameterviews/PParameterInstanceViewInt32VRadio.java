package axoloti.piccolo.parameterviews;

import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceInt32VRadio;
import axoloti.parameters.ParameterInt32VRadio;
import components.piccolo.PAssignMidiCCMenuItems;
import components.piccolo.control.PVRadioComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

public class PParameterInstanceViewInt32VRadio extends PParameterInstanceViewInt32 {

    public PParameterInstanceViewInt32VRadio(ParameterInstanceInt32VRadio parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public void updateV() {
        ctrl.setValue(parameterInstance.getValue().getInt());
    }

    @Override
    public PVRadioComponent CreateControl() {
        return new PVRadioComponent(0, ((ParameterInt32VRadio) parameterInstance.getParameter()).MaxValue.getInt(), axoObjectInstanceView);
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
