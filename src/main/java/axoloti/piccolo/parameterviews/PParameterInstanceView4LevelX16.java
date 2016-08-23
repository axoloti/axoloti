package axoloti.piccolo.parameterviews;

import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstance4LevelX16;
import components.piccolo.control.PCheckbox4StatesComponent;

public class PParameterInstanceView4LevelX16 extends PParameterInstanceViewInt32 {

    public PParameterInstanceView4LevelX16(ParameterInstance4LevelX16 parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public PCheckbox4StatesComponent CreateControl() {
        return new PCheckbox4StatesComponent(0, 16, axoObjectInstanceView);
    }

    @Override
    public PCheckbox4StatesComponent getControlComponent() {
        return (PCheckbox4StatesComponent) ctrl;
    }

    @Override
    public void ShowPreset(int i) {
    }

    @Override
    public void updateV() {
        ctrl.setValue(parameterInstance.getValue().getInt());
    }
}
