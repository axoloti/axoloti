package axoloti.piccolo.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstanceController;
import axoloti.patch.object.parameter.ParameterInstanceInt32BoxSmall;
import axoloti.piccolo.components.control.PNumberBoxComponent;

class PParameterInstanceViewInt32BoxSmall extends PParameterInstanceViewInt32Box {

    public PParameterInstanceViewInt32BoxSmall(ParameterInstanceController controller,
            IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public ParameterInstanceInt32BoxSmall getModel() {
        return (ParameterInstanceInt32BoxSmall) super.getModel();
    }

    @Override
    public PNumberBoxComponent CreateControl() {
        return new PNumberBoxComponent(0.0, getModel().getMinValue(),
                getModel().getMaxValue(), 1.0, 12, 12, axoObjectInstanceView);
    }
}
