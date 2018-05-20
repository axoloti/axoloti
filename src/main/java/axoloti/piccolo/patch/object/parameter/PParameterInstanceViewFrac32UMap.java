package axoloti.piccolo.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstanceFrac32UMap;
import axoloti.patch.object.parameter.preset.PresetDouble;
import axoloti.piccolo.components.PAssignMidiCCComponent;
import axoloti.piccolo.components.PAssignMidiCCMenuItems;
import axoloti.piccolo.components.PAssignModulatorComponent;
import axoloti.piccolo.components.PAssignModulatorMenuItems;
import axoloti.piccolo.components.PAssignPresetComponent;
import axoloti.piccolo.components.control.PDialComponent;
import axoloti.piccolo.patch.PatchPNode;
import axoloti.preferences.Theme;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import javax.swing.BoxLayout;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import org.piccolo2d.util.PPaintContext;

public class PParameterInstanceViewFrac32UMap extends PParameterInstanceViewFrac32U {

    PAssignModulatorComponent modulationAssign;
    PAssignPresetComponent presetAssign;

    public PParameterInstanceViewFrac32UMap(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
        initComponent();
    }

    private void initComponent() {

        PatchPNode btns = new PatchPNode(getPatchView());
        btns.setLayout(new BoxLayout(btns.getProxyComponent(), BoxLayout.PAGE_AXIS));

        midiAssign = new PAssignMidiCCComponent(getDModel(), getPatchView());
        btns.addChild(midiAssign);

        // TODO: piccolo reintroduce modulator button
        // modulationAssign = new PAssignModulatorComponent(this);
        // btns.addChild(modulationAssign);
        presetAssign = new PAssignPresetComponent(getDModel(), getPatchView());
        btns.addChild(presetAssign);
        addChild(btns);

        addInputEventListener(popupMouseListener);

    }

    @Override
    public ParameterInstanceFrac32UMap getDModel() {
        return (ParameterInstanceFrac32UMap) super.getDModel();
    }

    @Override
    public PDialComponent createControl() {
        PDialComponent d = new PDialComponent(
                0.0,
                getDModel().getMin(),
                getDModel().getMax(),
                getDModel().getTick(), axoObjectInstanceView);
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
            PresetDouble p = getDModel().getPreset(presetEditActive);
            if (p != null) {
                setPaint(Theme.getCurrentTheme().Parameter_Preset_Highlight);
                ctrl.setValue(p.getValue());
            } else {
                setPaint(Theme.getCurrentTheme().Parameter_Default_Background);
                ctrl.setValue(getDModel().getValue());
            }
        } else {
            setPaint(Theme.getCurrentTheme().Parameter_Default_Background);
            ctrl.setValue(getDModel().getValue());
        }
    }

    @Override
    public void populatePopup(JPopupMenu m) {
        super.populatePopup(m);
        JMenu m1 = new JMenu("Midi CC");
        new PAssignMidiCCMenuItems(this, m1);
        m.add(m1);
        JMenu m2 = new JMenu("Modulation");
        new PAssignModulatorMenuItems(this, m2);
        m.add(m2);
    }

    @Override
    public PDialComponent getControlComponent() {
        return (PDialComponent) ctrl;
    }

    @Override
    public void updateModulation(int index, double amount) {
        getDModel().updateModulation(index, amount);
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        super.paint(paintContext);
        Graphics2D g2 = paintContext.getGraphics();

        // TODO: piccolo onParent support
        // if (parameterInstance.getOnParent()) {
        //     ctrl.setForeground(Theme.getCurrentTheme().Parameter_On_Parent_Highlight);
        // } else {
            ctrl.setForeground(Theme.getCurrentTheme().Parameter_Default_Foreground);
//        }
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (ParameterInstance.PRESETS.is(evt)) {
            presetAssign.repaint();
        }
    }
}
