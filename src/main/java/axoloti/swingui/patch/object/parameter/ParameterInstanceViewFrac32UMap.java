package axoloti.swingui.patch.object.parameter;

import axoloti.preset.Preset;
import axoloti.preferences.Theme;
import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstanceController;
import axoloti.patch.object.parameter.ParameterInstanceFrac32UMap;
import axoloti.swingui.components.AssignMidiCCComponent;
import axoloti.swingui.components.AssignModulatorComponent;
import axoloti.swingui.components.AssignModulatorMenuItems;
import axoloti.swingui.components.AssignPresetComponent;
import axoloti.swingui.components.control.DialComponent;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import javax.swing.BoxLayout;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

class ParameterInstanceViewFrac32UMap extends ParameterInstanceViewFrac32U {

    AssignModulatorComponent modulationAssign;
    AssignPresetComponent presetAssign;

    public ParameterInstanceViewFrac32UMap(ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public ParameterInstanceFrac32UMap getModel() {
        return (ParameterInstanceFrac32UMap) super.getModel();
    }

    @Override
    public DialComponent CreateControl() {
        DialComponent d = new DialComponent(
                0.0,
                getModel().getMin(),
                getModel().getMax(),
                getModel().getTick());
        d.setNative(getModel().getConvs());
        return d;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        JPanel btns = new JPanel();
        btns.setBackground(Theme.getCurrentTheme().Object_Default_Background);
        btns.setLayout(new BoxLayout(btns, BoxLayout.PAGE_AXIS));

        midiAssign = new AssignMidiCCComponent(getController());
        btns.add(midiAssign);
// FIXME: reintroduce modulator button
//        modulationAssign = new AssignModulatorComponent(this);
//        btns.add(modulationAssign);
        presetAssign = new AssignPresetComponent(getController());
        btns.add(presetAssign);
        add(btns);

//        setComponentPopupMenu(new ParameterInstanceUInt7MapPopupMenu3(this));
        addMouseListener(popupMouseListener);
//        ctrl.setValue(getModel().getValue().getDouble());
    }


    /*
     *  Preset logic
     */
    @Override
    public void ShowPreset(int i) {
        this.presetEditActive = i;
        if (i > 0) {
            Preset p = getModel().getPreset(presetEditActive);
            if (p != null) {
                setBackground(Theme.getCurrentTheme().Parameter_Preset_Highlight);
                ctrl.setValue((Double)p.getValue());
            } else {
                setBackground(Theme.getCurrentTheme().Parameter_Default_Background);
                ctrl.setValue(getModel().getValue());
            }
        } else {
            setBackground(Theme.getCurrentTheme().Parameter_Default_Background);
            ctrl.setValue(getModel().getValue());
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
        new AssignModulatorMenuItems(getModel(), m2);
        m.add(m2);
    }

    @Override
    public DialComponent getControlComponent() {
        return (DialComponent) ctrl;
    }

    @Override
    public void updateModulation(int index, double amount) {
        getModel().updateModulation(index, amount);
        if (modulationAssign != null) {
            modulationAssign.repaint();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
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
