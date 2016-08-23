package axoloti.parameterviews;

import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceInt32BoxSmall;
import components.control.NumberBoxComponent;

public class ParameterInstanceViewInt32BoxSmall extends ParameterInstanceViewInt32Box {

    public ParameterInstanceViewInt32BoxSmall(ParameterInstanceInt32BoxSmall parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public ParameterInstanceInt32BoxSmall getParameterInstance() {
        return (ParameterInstanceInt32BoxSmall) parameterInstance;
    }

    @Override
    public NumberBoxComponent CreateControl() {
        return new NumberBoxComponent(0.0, getParameterInstance().getMin(), getParameterInstance().getMax(), 1.0, 12, 12);
    }
}
