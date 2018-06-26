package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstance4LevelX16;
import axoloti.patch.object.parameter.preset.PresetInt;
import axoloti.swingui.components.control.Checkbox4StatesComponent;

class ParameterInstanceView4LevelX16 extends ParameterInstanceView {

    ParameterInstanceView4LevelX16(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
        initCtrlComponent(ctrl);
    }

    @Override
    public ParameterInstance4LevelX16 getDModel() {
        return (ParameterInstance4LevelX16) super.getDModel();
    }

    @Override
    public boolean handleAdjustment() {
        int presetEdit = getPresetEditActive();
        PresetInt p = getDModel().getPreset(presetEdit);
        int value = (int) getControlComponent().getValue();
        if (p != null) {
            getDModel().getController().addPreset(presetEdit, value);
        } else if (getDModel().getValue() != (int) getControlComponent().getValue()) {
            int v = (int) getControlComponent().getValue();
            getDModel().getController().changeValue(v);
        } else {
            return false;
        }
        return true;
    }

    @Override
    public Checkbox4StatesComponent createControl() {
        return new Checkbox4StatesComponent(0, 16);
    }

    private final Checkbox4StatesComponent ctrl = createControl();

    @Override
    public Checkbox4StatesComponent getControlComponent() {
        return ctrl;
    }

}
