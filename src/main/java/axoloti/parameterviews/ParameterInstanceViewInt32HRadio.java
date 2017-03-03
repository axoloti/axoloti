package axoloti.parameterviews;

import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceInt32HRadio;
import axoloti.parameters.ParameterInt32HRadio;
import components.AssignMidiCCMenuItems;
import components.control.HRadioComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

public class ParameterInstanceViewInt32HRadio extends ParameterInstanceViewInt32 {

    public ParameterInstanceViewInt32HRadio(ParameterInstanceInt32HRadio parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public void updateV() {
        ctrl.setValue(parameterInstance.getValue().getInt());
    }

    @Override
    public HRadioComponent CreateControl() {
        return new HRadioComponent(0, ((ParameterInt32HRadio) parameterInstance.getParameter()).MaxValue.getInt());
    }

    @Override
    public HRadioComponent getControlComponent() {
        return (HRadioComponent) ctrl;
    }

    @Override
    public void populatePopup(JPopupMenu m) {
        super.populatePopup(m);
        JMenu m1 = new JMenu("Midi CC");
        new AssignMidiCCMenuItems(this, m1);
        m.add(m1);
    }
}
