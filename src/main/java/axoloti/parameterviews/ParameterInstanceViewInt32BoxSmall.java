package axoloti.parameterviews;

import axoloti.parameters.ParameterInstanceInt32BoxSmall;
import components.control.NumberBoxComponent;

public class ParameterInstanceViewInt32BoxSmall extends ParameterInstanceViewInt32Box {

    public ParameterInstanceViewInt32BoxSmall(ParameterInstanceInt32BoxSmall parameterInstance) {
        super(parameterInstance);
    }

    @Override
    public ParameterInstanceInt32BoxSmall getParameterInstance() {
        return (ParameterInstanceInt32BoxSmall) this.parameterInstance;
    }

    @Override
    public NumberBoxComponent CreateControl() {
        return new NumberBoxComponent(0.0, getParameterInstance().getMin(), getParameterInstance().getMax(), 1.0, 12, 12);
    }
}