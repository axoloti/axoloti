package axoloti.parameterviews;

import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceController;
import axoloti.parameters.ParameterInstanceInt32BoxSmall;
import components.control.NumberBoxComponent;

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
