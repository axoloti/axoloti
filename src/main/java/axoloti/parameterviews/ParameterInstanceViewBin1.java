package axoloti.parameterviews;

import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceController;
import components.AssignMidiCCMenuItems;
import components.control.CheckboxComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

class ParameterInstanceViewBin1 extends ParameterInstanceViewBin {

    public ParameterInstanceViewBin1(ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public CheckboxComponent CreateControl() {
        return new CheckboxComponent(0, 1);
    }

    @Override
    public void ShowPreset(int i) {
    }

    @Override
    public void updateV() {
        ctrl.setValue(getModel().getValue().getInt());
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
