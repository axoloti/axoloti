package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstanceController;
import axoloti.swingui.components.control.Checkbox4StatesComponent;

class ParameterInstanceView4LevelX16 extends ParameterInstanceViewInt32 {

    public ParameterInstanceView4LevelX16(ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
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

}
