package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstanceFrac32UMap;
import axoloti.patch.object.parameter.preset.Preset;
import axoloti.preferences.Theme;
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
        btns.setBackground(Theme.getCurrentTheme().Object_Default_Background);
        btns.setLayout(new BoxLayout(btns, BoxLayout.PAGE_AXIS));

        midiAssign = new AssignMidiCCComponent(getDModel());
        btns.add(midiAssign);
// FIXME: reintroduce modulator button
//        modulationAssign = new AssignModulatorComponent(this);
//        btns.add(modulationAssign);
        presetAssign = new AssignPresetComponent(getDModel());
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

    /*
     *  Preset logic
     */
    @Override
    public void showPreset(int i) {
        this.presetEditActive = i;
        if (i > 0) {
            Preset p = getDModel().getPreset(presetEditActive);
            if (p != null) {
                setBackground(Theme.getCurrentTheme().Parameter_Preset_Highlight);
                ctrl.setValue((Double)p.getValue());
            } else {
                setBackground(Theme.getCurrentTheme().Parameter_Default_Background);
                ctrl.setValue(getDModel().getValue());
            }
        } else {
            setBackground(Theme.getCurrentTheme().Parameter_Default_Background);
            ctrl.setValue(getDModel().getValue());
        }
        /*
         if ((presets != null) && (!presets.isEmpty())) {
         lblPreset.setVisible(true);
         } else {
         lblPreset.setVisible(false);
         }
         */
    }

    @Override
    public void populatePopup(JPopupMenu m) {
        super.populatePopup(m);
        JMenu m2 = new JMenu("Modulation");
        // FIXME : reintroduce midi/modulation popup menu
        new AssignModulatorMenuItems(getDModel(), m2);
        m.add(m2);
    }

    private final DialComponent ctrl = createControl();

    @Override
    public DialComponent getControlComponent() {
        return ctrl;
    }

    @Override
    public void updateModulation(int index, double amount) {
        getDModel().updateModulation(index, amount);
        if (modulationAssign != null) {
            modulationAssign.repaint();
        }
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
        }
    }

}
