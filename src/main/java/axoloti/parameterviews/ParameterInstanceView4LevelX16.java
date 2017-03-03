package axoloti.parameterviews;

import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstance4LevelX16;
import components.control.Checkbox4StatesComponent;

public class ParameterInstanceView4LevelX16 extends ParameterInstanceViewInt32 {

    public ParameterInstanceView4LevelX16(ParameterInstance4LevelX16 parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public Checkbox4StatesComponent CreateControl() {
        return new Checkbox4StatesComponent(0, 16);
    }

    @Override
    public Checkbox4StatesComponent getControlComponent() {
        return (Checkbox4StatesComponent) ctrl;
    }

    @Override
    public void ShowPreset(int i) {
    }

    @Override
    public void updateV() {
        ctrl.setValue(parameterInstance.getValue().getInt());
    }
}
