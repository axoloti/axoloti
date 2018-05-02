package axoloti.piccolo.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstanceController;
import axoloti.patch.object.parameter.ParameterInstanceFrac32UMap;
import axoloti.piccolo.components.PAssignMidiCCComponent;
import axoloti.piccolo.components.PAssignMidiCCMenuItems;
import axoloti.piccolo.components.PAssignModulatorComponent;
import axoloti.piccolo.components.PAssignModulatorMenuItems;
import axoloti.piccolo.components.PAssignPresetComponent;
import axoloti.piccolo.components.control.PDialComponent;
import axoloti.piccolo.patch.PatchPNode;
import axoloti.preferences.Theme;
import axoloti.patch.object.parameter.preset.PresetDouble;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import javax.swing.BoxLayout;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import org.piccolo2d.util.PPaintContext;

public class PParameterInstanceViewFrac32UMap extends PParameterInstanceViewFrac32U {

    PAssignModulatorComponent modulationAssign;
    PAssignPresetComponent presetAssign;

    public PParameterInstanceViewFrac32UMap(ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
        initComponent();
    }

    private void initComponent() {

        PatchPNode btns = new PatchPNode(getPatchView());
        btns.setLayout(new BoxLayout(btns.getProxyComponent(), BoxLayout.PAGE_AXIS));

        midiAssign = new PAssignMidiCCComponent(getController(), getPatchView());
        btns.addChild(midiAssign);

        // FIXME: reintroduce modulator button
        // modulationAssign = new PAssignModulatorComponent(this);
        // btns.addChild(modulationAssign);
        presetAssign = new PAssignPresetComponent(getController(), getPatchView());
        btns.addChild(presetAssign);
        addChild(btns);

        addInputEventListener(popupMouseListener);

    }

    @Override
    public ParameterInstanceFrac32UMap getModel() {
        return (ParameterInstanceFrac32UMap) super.getModel();
    }

    @Override
    public PDialComponent CreateControl() {
        PDialComponent d = new PDialComponent(
                0.0,
                getModel().getMin(),
                getModel().getMax(),
                getModel().getTick(), axoObjectInstanceView);
        d.setNative(getModel().getConvs());
        return d;
    }

    /*
     *  Preset logic
     */
    @Override
    public void ShowPreset(int i) {
        this.presetEditActive = i;
        if (i > 0) {
            PresetDouble p = getModel().getPreset(presetEditActive);
            if (p != null) {
                setPaint(Theme.getCurrentTheme().Parameter_Preset_Highlight);
                ctrl.setValue(p.getValue());
            } else {
                setPaint(Theme.getCurrentTheme().Parameter_Default_Background);
                ctrl.setValue(getModel().getValue());
            }
        } else {
            setPaint(Theme.getCurrentTheme().Parameter_Default_Background);
            ctrl.setValue(getModel().getValue());
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
        getModel().updateModulation(index, amount);
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        super.paint(paintContext);
        Graphics2D g2 = paintContext.getGraphics();

        // TODO onParent support
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
