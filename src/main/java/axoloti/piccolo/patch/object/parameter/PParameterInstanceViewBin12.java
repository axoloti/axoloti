package axoloti.piccolo.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.piccolo.components.PAssignMidiCCMenuItems;
import axoloti.piccolo.components.control.PCheckboxComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

class PParameterInstanceViewBin12 extends PParameterInstanceViewBin {

    PParameterInstanceViewBin12(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public PCheckboxComponent createControl() {
        return new PCheckboxComponent(0, 12, axoObjectInstanceView);
    }

    @Override
    public PCheckboxComponent getControlComponent() {
        return (PCheckboxComponent) ctrl;
    }

    @Override
    public void populatePopup(JPopupMenu m) {
        super.populatePopup(m);
        JMenu m1 = new JMenu("Midi CC");
        // assignMidiCCMenuItems, does stuff in ctor
        PAssignMidiCCMenuItems assignMidiCCMenuItems = new PAssignMidiCCMenuItems(this, m1);
        m.add(m1);
    }
}
