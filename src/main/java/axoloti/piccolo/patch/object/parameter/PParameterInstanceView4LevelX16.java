package axoloti.piccolo.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstanceController;
import axoloti.piccolo.components.control.PCheckbox4StatesComponent;

public class PParameterInstanceView4LevelX16 extends PParameterInstanceViewInt32 {

    public PParameterInstanceView4LevelX16(ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
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
}
