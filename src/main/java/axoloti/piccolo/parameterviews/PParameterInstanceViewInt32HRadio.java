package axoloti.piccolo.parameterviews;

import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceInt32HRadio;
import axoloti.parameters.ParameterInt32HRadio;
import components.piccolo.PAssignMidiCCMenuItems;
import components.piccolo.control.PHRadioComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

public class PParameterInstanceViewInt32HRadio extends PParameterInstanceViewInt32 {

    public PParameterInstanceViewInt32HRadio(ParameterInstanceInt32HRadio parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public void updateV() {
        ctrl.setValue(getModel().getValue());
    }

    @Override
    public PHRadioComponent CreateControl() {
        return null;//new PHRadioComponent(0, ((ParameterInt32HRadio) parameterInstance.getModel()).MaxValue, axoObjectInstanceView);
    }

    @Override
    public PHRadioComponent getControlComponent() {
        return (PHRadioComponent) ctrl;
    }

    @Override
    public void populatePopup(JPopupMenu m) {
        super.populatePopup(m);
        JMenu m1 = new JMenu("Midi CC");
        new PAssignMidiCCMenuItems(this, m1);
        m.add(m1);
    }
}
