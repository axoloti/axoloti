package axoloti.swingui.patch.object.parameter;

import axoloti.preferences.Theme;
import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstanceBin;
import axoloti.patch.object.parameter.ParameterInstanceController;
import axoloti.preset.PresetInt;
import java.beans.PropertyChangeEvent;

abstract class ParameterInstanceViewBin extends ParameterInstanceView {

    ParameterInstanceViewBin(ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public ParameterInstanceBin getModel() {
        return (ParameterInstanceBin) getController().getModel();
    }

    @Override
    public void ShowPreset(int i) {
        presetEditActive = i;
        if (i > 0) {
            PresetInt p = getModel().getPreset(presetEditActive);
            if (p != null) {
                setBackground(Theme.getCurrentTheme().Parameter_Preset_Highlight);
                getControlComponent().setValue(p.getValue());
            } else {
                setBackground(Theme.getCurrentTheme().Parameter_Default_Background);
                getControlComponent().setValue(getModel().getValue());
            }
        } else {
            setBackground(Theme.getCurrentTheme().Parameter_Default_Background);
            getControlComponent().setValue(getModel().getValue());
        }
    }

    @Override
    public boolean handleAdjustment() {
        PresetInt p = getModel().getPreset(presetEditActive);
        if (p != null) {
            p.setValue((int) getControlComponent().getValue());
        } else if (getModel().getValue() != (int) getControlComponent().getValue()) {
            if (getController() != null) {
                Integer vi32 = (int)getControlComponent().getValue();
                getController().setModelUndoableProperty(ParameterInstance.VALUE, vi32);
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
