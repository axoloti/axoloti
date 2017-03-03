package axoloti.piccolo.parameterviews;

import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceInt32BoxSmall;
import components.piccolo.control.PNumberBoxComponent;

public class PParameterInstanceViewInt32BoxSmall extends PParameterInstanceViewInt32Box {

    public PParameterInstanceViewInt32BoxSmall(ParameterInstanceInt32BoxSmall parameterInstance,
            IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public ParameterInstanceInt32BoxSmall getParameterInstance() {
        return (ParameterInstanceInt32BoxSmall) parameterInstance;
    }

    @Override
    public PNumberBoxComponent CreateControl() {
        return new PNumberBoxComponent(0.0, getParameterInstance().getMin(),
                getParameterInstance().getMax(), 1.0, 12, 12, axoObjectInstanceView);
    }
}
