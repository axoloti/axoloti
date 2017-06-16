package axoloti.parameterviews;

import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceController;
import axoloti.parameters.ParameterInstanceInt32Box;
import components.control.NumberBoxComponent;

class ParameterInstanceViewInt32Box extends ParameterInstanceViewInt32 {

    public ParameterInstanceViewInt32Box(ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public ParameterInstanceInt32Box getModel() {
        return (ParameterInstanceInt32Box) super.getModel();
    }

    @Override
    public void updateV() {
        ctrl.setValue(getModel().getValue().getInt());
    }

    @Override
    public NumberBoxComponent CreateControl() {
        NumberBoxComponent n = new NumberBoxComponent(0.0, getModel().getMin(), getModel().getMax(), 1.0);
        return n;
    }

    @Override
    public NumberBoxComponent getControlComponent() {
        return (NumberBoxComponent) ctrl;
    }
}
