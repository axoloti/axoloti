package axoloti.piccolo.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstanceBin;
import axoloti.patch.object.parameter.preset.PresetInt;
import axoloti.preferences.Theme;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import org.piccolo2d.util.PPaintContext;

abstract class PParameterInstanceViewBin extends PParameterInstanceView {

    PParameterInstanceViewBin(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);

    }

    @Override
    public ParameterInstanceBin getDModel() {
        return (ParameterInstanceBin) super.getDModel();
    }

    @Override
    public void showPreset(int i) {
        presetEditActive = i;
        if (i > 0) {
            PresetInt p = getDModel().getPreset(presetEditActive);
            if (p != null) {
                setPaint(Theme.getCurrentTheme().Parameter_Preset_Highlight);
                getControlComponent().setValue(p.getValue());
            } else {
                setPaint(Theme.getCurrentTheme().Parameter_Default_Background);
                getControlComponent().setValue(getDModel().getValue());
            }
        } else {
            setPaint(Theme.getCurrentTheme().Parameter_Default_Background);
            getControlComponent().setValue(getDModel().getValue());
        }
    }

    @Override
    public boolean handleAdjustment() {
        PresetInt p = getDModel().getPreset(presetEditActive);
        if (p != null) {
            p.setValue((int) getControlComponent().getValue());
        } else if (getDModel().getValue() != (int) getControlComponent().getValue()) {
            if (getParameterInstanceController() != null) {
                Integer vi32 = (int) getControlComponent().getValue();
                getParameterInstanceController().changeValue(vi32);
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();

//        if (parameterInstance.getOnParent()) {
//            ctrl.setForeground(Theme.getCurrentTheme().Parameter_On_Parent_Highlight);
//        } else {
            ctrl.setForeground(Theme.getCurrentTheme().Parameter_Default_Foreground);
//        }
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (ParameterInstanceBin.VALUE.is(evt)) {
            Integer v = (Integer)evt.getNewValue();
            ctrl.setValue(v);
        }
    }
}
