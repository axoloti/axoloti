package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.swingui.components.control.Checkbox4StatesComponent;

class ParameterInstanceView4LevelX16 extends ParameterInstanceViewInt32 {

    public ParameterInstanceView4LevelX16(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public Checkbox4StatesComponent createControl() {
        return new Checkbox4StatesComponent(0, 16);
    }

    @Override
    public Checkbox4StatesComponent getControlComponent() {
        return (Checkbox4StatesComponent) ctrl;
    }

    @Override
    public void showPreset(int i) {
    }

}
