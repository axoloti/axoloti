package axoloti.parameterviews;

import axoloti.Preset;
import axoloti.Theme;
import axoloti.datatypes.ValueFrac32;
import axoloti.datatypes.ValueInt32;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceController;
import static axoloti.parameters.ParameterInstanceController.ELEMENT_PARAM_VALUE;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;

abstract class ParameterInstanceViewBin extends ParameterInstanceView {

    ParameterInstanceViewBin(ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public void ShowPreset(int i) {
        presetEditActive = i;
        if (i > 0) {
            Preset p = getModel().GetPreset(presetEditActive);
            if (p != null) {
                setBackground(Theme.getCurrentTheme().Parameter_Preset_Highlight);
                getControlComponent().setValue(p.value.getDouble());
            } else {
                setBackground(Theme.getCurrentTheme().Parameter_Default_Background);
                getControlComponent().setValue(getModel().getValue().getDouble());
            }
        } else {
            setBackground(Theme.getCurrentTheme().Parameter_Default_Background);
            getControlComponent().setValue(getModel().getValue().getDouble());
        }
    }

    @Override
    public boolean handleAdjustment() {
        Preset p = getModel().GetPreset(presetEditActive);
        if (p != null) {
            p.value = new ValueInt32((int) getControlComponent().getValue());
        } else if (getModel().getValue().getInt() != (int) getControlComponent().getValue()) {
            if (controller != null) {
                ValueInt32 vi32 = new ValueInt32((int)getControlComponent().getValue());
                getController().setModelUndoableProperty(ELEMENT_PARAM_VALUE, vi32);
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (evt.getPropertyName().equals(ParameterInstanceController.ELEMENT_PARAM_VALUE)) {
            ValueInt32 v = (ValueInt32)evt.getNewValue();
            ctrl.setValue(v.getInt());
        }
    }
}
