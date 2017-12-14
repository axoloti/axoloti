package axoloti.parameterviews;

import axoloti.PresetInt;
import axoloti.Theme;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstance;
import axoloti.parameters.ParameterInstanceController;
import axoloti.parameters.ParameterInstanceInt32;
import axoloti.parameters.ParameterInt32;
import java.beans.PropertyChangeEvent;

abstract class ParameterInstanceViewInt32 extends ParameterInstanceView {

    ParameterInstanceViewInt32(ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public ParameterInstanceInt32 getModel() {
        return (ParameterInstanceInt32)controller.getModel();
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
            p.setValue((int)getControlComponent().getValue());
        } else if (getModel().getValue() != (int) getControlComponent().getValue()) {
            int v = (int)getControlComponent().getValue();
            getController().setModelUndoableProperty(ParameterInstance.VALUE, v);
        } else {
            return false;
        }
        return true;
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
