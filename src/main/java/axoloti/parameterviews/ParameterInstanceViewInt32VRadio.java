package axoloti.parameterviews;

import axoloti.parameters.ParameterInstanceInt32VRadio;
import axoloti.parameters.ParameterInt32VRadio;
import components.AssignMidiCCMenuItems;
import components.control.VRadioComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

public class ParameterInstanceViewInt32VRadio extends ParameterInstanceViewInt32 {

    public ParameterInstanceViewInt32VRadio(ParameterInstanceInt32VRadio parameterInstance) {
        super(parameterInstance);
    }
    
    @Override
    public void updateV() {
        ctrl.setValue(parameterInstance.getValue().getInt());
    }
    
    
    @Override
    public VRadioComponent CreateControl() {
        return new VRadioComponent(0, ((ParameterInt32VRadio) parameterInstance.getParameter()).MaxValue.getInt());
    }

    @Override
    public VRadioComponent getControlComponent() {
        return (VRadioComponent) ctrl;
    }

    @Override
    public void populatePopup(JPopupMenu m) {
        super.populatePopup(m);
        JMenu m1 = new JMenu("Midi CC");
        // AssignMidiCCMenuItems, does stuff in ctor
        AssignMidiCCMenuItems assignMidiCCMenuItems = new AssignMidiCCMenuItems(this, m1);
        m.add(m1);
    }
}