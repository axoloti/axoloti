package axoloti.parameterviews;

import axoloti.Preset;
import axoloti.Theme;
import axoloti.datatypes.Value;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceFrac32UMap;
import components.AssignMidiCCComponent;
import components.AssignMidiCCMenuItems;
import components.AssignModulatorComponent;
import components.AssignModulatorMenuItems;
import components.AssignPresetComponent;
import components.control.DialComponent;
import java.awt.Graphics;
import javax.swing.BoxLayout;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class ParameterInstanceViewFrac32UMap extends ParameterInstanceViewFrac32U {

    AssignModulatorComponent modulationAssign;
    AssignPresetComponent presetAssign;

    public ParameterInstanceViewFrac32UMap(ParameterInstanceFrac32UMap parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public ParameterInstanceFrac32UMap getParameterInstance() {
        return (ParameterInstanceFrac32UMap) parameterInstance;
    }

    @Override
    public DialComponent CreateControl() {
        DialComponent d = new DialComponent(
                0.0,
                getParameterInstance().getMin(),
                getParameterInstance().getMax(),
                getParameterInstance().getTick());
        d.setNative(getParameterInstance().getConvs());
        return d;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        JPanel btns = new JPanel();
        btns.setBackground(Theme.getCurrentTheme().Object_Default_Background);
        btns.setLayout(new BoxLayout(btns, BoxLayout.PAGE_AXIS));

        //lblCC = new LabelComponent("C");
        //btns.add(lblCC);
        midiAssign = new AssignMidiCCComponent(this);
        btns.add(midiAssign);
        modulationAssign = new AssignModulatorComponent(this);
        btns.add(modulationAssign);
        presetAssign = new AssignPresetComponent(this);
        btns.add(presetAssign);
        add(btns);

//        setComponentPopupMenu(new ParameterInstanceUInt7MapPopupMenu3(this));
        addMouseListener(popupMouseListener);
        updateV();
    }

    @Override
    public void updateV() {
        super.updateV();
        if (ctrl != null) {
            ctrl.setValue(getParameterInstance().getValue().getDouble());
        }
    }

    /*
     *  Preset logic
     */
    @Override
    public void ShowPreset(int i) {
        this.presetEditActive = i;
        if (i > 0) {
            Preset p = getParameterInstance().GetPreset(presetEditActive);
            if (p != null) {
                setBackground(Theme.getCurrentTheme().Parameter_Preset_Highlight);
                ctrl.setValue(p.value.getDouble());
            } else {
                setBackground(Theme.getCurrentTheme().Parameter_Default_Background);
                ctrl.setValue(getParameterInstance().getValue().getDouble());
            }
        } else {
            setBackground(Theme.getCurrentTheme().Parameter_Default_Background);
            ctrl.setValue(getParameterInstance().getValue().getDouble());
        }
        presetAssign.repaint();
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
        JMenu m1 = new JMenu("Midi CC");
        new AssignMidiCCMenuItems(this, m1);
        m.add(m1);
        JMenu m2 = new JMenu("Modulation");
        new AssignModulatorMenuItems(this, m2);
        m.add(m2);
    }

    @Override
    public DialComponent getControlComponent() {
        return (DialComponent) ctrl;
    }

    @Override
    public void updateModulation(int index, double amount) {
        getParameterInstance().updateModulation(index, amount);
        if (modulationAssign != null) {
            modulationAssign.repaint();
        }
    }

    @Override
    public Preset AddPreset(int index, Value value) {
        Preset p = getParameterInstance().AddPreset(index, value);
        presetAssign.repaint();
        return p;
    }

    @Override
    public void RemovePreset(int index) {
        getParameterInstance().RemovePreset(index);
        presetAssign.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        if (parameterInstance.isOnParent()) {
            setForeground(Theme.getCurrentTheme().Parameter_On_Parent_Highlight);
        } else {
            setForeground(Theme.getCurrentTheme().Parameter_Default_Foreground);
        }
        super.paintComponent(g);
    }
}
