package axoloti.piccolo.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.object.parameter.ParameterInt32;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstanceInt32;
import axoloti.patch.object.parameter.preset.PresetInt;
import axoloti.preferences.Theme;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import org.piccolo2d.util.PPaintContext;

abstract class PParameterInstanceViewInt32 extends PParameterInstanceView {

    PParameterInstanceViewInt32(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);

    }

    @Override
    public ParameterInstanceInt32 getDModel() {
        return (ParameterInstanceInt32) super.getDModel();
    }

    @Override
    public boolean handleAdjustment() {
        PresetInt p = null; //getDModel().getPreset(presetEditActive);
        if (p != null) { // TODO: piccolo fix preset editing logic
            //p.setValue((int) getControlComponent().getValue());
        } else if (getDModel().getValue() != (int) getControlComponent().getValue()) {
            int v = (int) getControlComponent().getValue();
            getParameterInstanceController().changeValue(v);
        } else {
            return false;
        }
        return true;
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();
        if (getDModel().getOnParent()) {
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
