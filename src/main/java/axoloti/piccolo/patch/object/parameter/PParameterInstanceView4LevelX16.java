package axoloti.piccolo.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.object.parameter.ParameterInt32;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstance4LevelX16;
import axoloti.patch.object.parameter.preset.PresetInt;
import axoloti.piccolo.components.control.PCheckbox4StatesComponent;
import java.beans.PropertyChangeEvent;

class PParameterInstanceView4LevelX16 extends PParameterInstanceView {

    PParameterInstanceView4LevelX16(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public PCheckbox4StatesComponent createControl() {
        return new PCheckbox4StatesComponent(0, 16, axoObjectInstanceView);
    }

    @Override
    public PCheckbox4StatesComponent getControlComponent() {
        return (PCheckbox4StatesComponent) ctrl;
    }

    @Override
    public ParameterInstance4LevelX16 getDModel() {
        return (ParameterInstance4LevelX16) super.getDModel();
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
