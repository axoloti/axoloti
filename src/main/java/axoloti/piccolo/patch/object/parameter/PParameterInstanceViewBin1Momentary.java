package axoloti.piccolo.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstanceController;
import axoloti.piccolo.components.control.PPulseButtonComponent;

public class PParameterInstanceViewBin1Momentary extends PParameterInstanceViewBin {

    public PParameterInstanceViewBin1Momentary(ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
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
