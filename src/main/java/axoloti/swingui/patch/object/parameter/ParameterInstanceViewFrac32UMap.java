package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstanceFrac32UMap;
import axoloti.swingui.components.AssignMidiCCComponent;
import axoloti.swingui.components.AssignModulatorComponent;
import axoloti.swingui.components.AssignModulatorMenuItems;
import axoloti.swingui.components.control.DialComponent;
import axoloti.swingui.patch.object.parameter.preset.AssignPresetComponent;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import javax.swing.BoxLayout;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

class ParameterInstanceViewFrac32UMap extends ParameterInstanceViewFrac32U {

    AssignModulatorComponent modulationAssign;
    AssignPresetComponent presetAssign;

    ParameterInstanceViewFrac32UMap(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
        initCtrlComponent(ctrl);
        initComponent();
    }

    private void initComponent() {

        JPanel btns = new JPanel();
        btns.setLayout(new BoxLayout(btns, BoxLayout.PAGE_AXIS));

        midiAssign = new AssignMidiCCComponent(getDModel());
        btns.add(midiAssign);
        modulationAssign = new AssignModulatorComponent(getDModel());
        btns.add(modulationAssign);
        presetAssign = new AssignPresetComponent(this);
        btns.add(presetAssign);
        add(btns);

//        setComponentPopupMenu(new ParameterInstanceUInt7MapPopupMenu3(this));
        addMouseListener(popupMouseListener);
//        ctrl.setValue(getModel().getValue().getDouble());
    }

    @Override
    public ParameterInstanceFrac32UMap getDModel() {
        return (ParameterInstanceFrac32UMap) super.getDModel();
    }

    @Override
    public DialComponent createControl() {
        DialComponent d = new DialComponent(
                0.0,
                getDModel().getMin(),
                getDModel().getMax(),
                getDModel().getTick());
        d.setNative(getDModel().getConvs());
        return d;
    }

    @Override
    public void populatePopup(JPopupMenu m) {
        super.populatePopup(m);
        JMenu m2 = new JMenu("Modulation");
        new AssignModulatorMenuItems(getDModel(), m2);
        m.add(m2);
    }

    private final DialComponent ctrl = createControl();

    @Override
    public DialComponent getControlComponent() {
        return ctrl;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (ParameterInstance.PRESETS.is(evt)) {
            presetAssign.repaint();
        } else if (ParameterInstance.MODULATIONS.is(evt)) {
            modulationAssign.repaint();
        }
    }

}
