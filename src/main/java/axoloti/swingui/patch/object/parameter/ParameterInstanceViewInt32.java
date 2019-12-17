package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.object.parameter.ParameterInt32;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstanceInt32;
import axoloti.patch.object.parameter.preset.PresetInt;
import axoloti.preferences.Theme;
import java.beans.PropertyChangeEvent;

abstract class ParameterInstanceViewInt32 extends ParameterInstanceView {

    ParameterInstanceViewInt32(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public ParameterInstanceInt32 getDModel() {
        return (ParameterInstanceInt32) super.getDModel();
    }

    @Override
    public void update() {
        int i = getPresetEditActive();
        if (i > 0) {
            PresetInt p = getDModel().getPreset(i);
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
        int presetEdit = getPresetEditActive();
        PresetInt p = getDModel().getPreset(presetEdit);
        int value = (int) getControlComponent().getValue();
        if (p != null) {
            getDModel().getController().addMetaUndo("change preset of parameter");
            getDModel().getController().addPreset(presetEdit, value);
        } else if (getDModel().getValue() != (int) getControlComponent().getValue()) {
            int v = (int)getControlComponent().getValue();
            getDModel().getController().changeValue(v);
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (ParameterInstance.VALUE.is(evt)) {
            update();
        } else if (ParameterInstance.CONVERSION.is(evt)) {
            update();
        } else if (ParameterInstance.PRESETS.is(evt)) {
            update();
        } else if (ParameterInt32.VALUE_MIN.is(evt)) {
//            ctrl.
        }

    }

}
