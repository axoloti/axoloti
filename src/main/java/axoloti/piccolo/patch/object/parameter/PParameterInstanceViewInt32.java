package axoloti.piccolo.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.object.parameter.ParameterInt32;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstanceController;
import axoloti.patch.object.parameter.ParameterInstanceInt32;
import axoloti.preferences.Theme;
import axoloti.preset.PresetInt;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import org.piccolo2d.util.PPaintContext;

public abstract class PParameterInstanceViewInt32 extends PParameterInstanceView {

    PParameterInstanceViewInt32(ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);

    }

    @Override
    public ParameterInstanceInt32 getModel() {
        return (ParameterInstanceInt32) controller.getModel();
    }

    @Override
    public void ShowPreset(int i) {
        presetEditActive = i;
        if (i > 0) {
            PresetInt p = getModel().getPreset(presetEditActive);
            if (p != null) {
                setPaint(Theme.getCurrentTheme().Parameter_Preset_Highlight);
                getControlComponent().setValue(p.getValue());
            } else {
                setPaint(Theme.getCurrentTheme().Parameter_Default_Background);
                getControlComponent().setValue(getModel().getValue());
            }
        } else {
            setPaint(Theme.getCurrentTheme().Parameter_Default_Background);
            getControlComponent().setValue(getModel().getValue());
        }
    }

    @Override
    public boolean handleAdjustment() {
        PresetInt p = getModel().getPreset(presetEditActive);
        if (p != null) {
            p.setValue((int) getControlComponent().getValue());
        } else if (getModel().getValue() != (int) getControlComponent().getValue()) {
            int v = (int) getControlComponent().getValue();
            getController().setModelUndoableProperty(ParameterInstance.VALUE, v);
        } else {
            return false;
        }
        return true;
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();
        if (getModel().getOnParent()) {
            ctrl.setForeground(Theme.getCurrentTheme().Parameter_On_Parent_Highlight);
        } else {
            ctrl.setForeground(Theme.getCurrentTheme().Parameter_Default_Foreground);
        }
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (ParameterInstance.VALUE.is(evt)) {
            int v = (Integer) evt.getNewValue();
            ctrl.setValue(v);
        } else if (ParameterInt32.VALUE_MIN.is(evt)) {
//            ctrl.
        }
    }
}
