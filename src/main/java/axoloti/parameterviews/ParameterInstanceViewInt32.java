package axoloti.parameterviews;

import axoloti.Preset;
import axoloti.Theme;
import axoloti.datatypes.ValueInt32;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstance;
import axoloti.parameters.ParameterInstanceController;
import java.beans.PropertyChangeEvent;

abstract class ParameterInstanceViewInt32 extends ParameterInstanceView {

    ParameterInstanceViewInt32(ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
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
                ValueInt32 vi32 = new ValueInt32((int)getControlComponent().getValue());
                getController().setModelUndoableProperty(ParameterInstance.ELEMENT_PARAM_VALUE, vi32);
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (evt.getPropertyName().equals(ParameterInstance.ELEMENT_PARAM_VALUE)) {
            ValueInt32 v = (ValueInt32) evt.getNewValue();
            ctrl.setValue(v.getInt());
        }
    }
    
}
