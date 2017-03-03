package axoloti.piccolo.parameterviews;

import axoloti.datatypes.Value;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceBin1Momentary;
import components.piccolo.control.PPulseButtonComponent;

public class PParameterInstanceViewBin1Momentary extends PParameterInstanceViewInt32 {

    public PParameterInstanceViewBin1Momentary(ParameterInstanceBin1Momentary parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public void updateV() {
        ctrl.setValue(parameterInstance.getValue().getInt());
    }

    @Override
    public void setValue(Value value) {
        parameterInstance.setValue(value);
        updateV();
    }

    @Override
    public PPulseButtonComponent CreateControl() {
        return new PPulseButtonComponent(axoObjectInstanceView);
    }

    @Override
    public PPulseButtonComponent getControlComponent() {
        return (PPulseButtonComponent) ctrl;
    }
}
