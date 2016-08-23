package axoloti.parameterviews;

import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceBin12;
import components.AssignMidiCCMenuItems;
import components.control.CheckboxComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

public class ParameterInstanceViewBin12 extends ParameterInstanceViewInt32 {

    public ParameterInstanceViewBin12(ParameterInstanceBin12 parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public CheckboxComponent CreateControl() {
        return new CheckboxComponent(0, 12);
    }

    @Override
    public void ShowPreset(int i) {
    }

    @Override
    public void updateV() {
        ctrl.setValue(parameterInstance.getValue().getInt());
    }

    @Override
    public CheckboxComponent getControlComponent() {
        return (CheckboxComponent) ctrl;
    }

    @Override
    public void populatePopup(JPopupMenu m) {
        super.populatePopup(m);
        JMenu m1 = new JMenu("Midi CC");
        // assignMidiCCMenuItems, does stuff in ctor
        AssignMidiCCMenuItems assignMidiCCMenuItems = new AssignMidiCCMenuItems(this, m1);
        m.add(m1);
    }
}
