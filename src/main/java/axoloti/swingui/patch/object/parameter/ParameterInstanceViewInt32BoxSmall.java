package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstanceController;
import axoloti.patch.object.parameter.ParameterInstanceInt32BoxSmall;
import axoloti.swingui.components.control.NumberBoxComponent;

class ParameterInstanceViewInt32BoxSmall extends ParameterInstanceViewInt32Box {

    public ParameterInstanceViewInt32BoxSmall(ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public ParameterInstanceInt32BoxSmall getModel() {
        return (ParameterInstanceInt32BoxSmall) super.getModel();
    }

    @Override
    public NumberBoxComponent CreateControl() {
        return new NumberBoxComponent(0.0, getModel().getMinValue(), getModel().getMaxValue(), 1.0, 12, 12);
    }
}
