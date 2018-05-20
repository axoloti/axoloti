package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstanceBin;
import axoloti.patch.object.parameter.preset.PresetInt;
import axoloti.preferences.Theme;
import java.beans.PropertyChangeEvent;

abstract class ParameterInstanceViewBin extends ParameterInstanceView {

    ParameterInstanceViewBin(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
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
                setBackground(Theme.getCurrentTheme().Parameter_Preset_Highlight);
                getControlComponent().setValue(p.getValue());
            } else {
                setBackground(Theme.getCurrentTheme().Parameter_Default_Background);
                getControlComponent().setValue(getDModel().getValue());
            }
        } else {
            setBackground(Theme.getCurrentTheme().Parameter_Default_Background);
            getControlComponent().setValue(getDModel().getValue());
        }
    }

    @Override
    public boolean handleAdjustment() {
        PresetInt p = getDModel().getPreset(presetEditActive);
        if (p != null) {
            p.setValue((int) getControlComponent().getValue());
        } else if (getDModel().getValue() != (int) getControlComponent().getValue()) {
            if (getDModel().getController() != null) {
                Integer vi32 = (int)getControlComponent().getValue();
                getDModel().getController().changeValue(vi32);
            }
        } else {
            return false;
        }
        return true;
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
