package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstanceController;
import axoloti.patch.object.parameter.ParameterInstanceFrac32SMapVSlider;
import axoloti.preferences.Theme;
import axoloti.preset.Preset;
import axoloti.swingui.components.control.VSliderComponent;

class ParameterInstanceViewFrac32SMapVSlider extends ParameterInstanceViewFrac32S {

    public ParameterInstanceViewFrac32SMapVSlider(ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public ParameterInstanceFrac32SMapVSlider getModel() {
        return (ParameterInstanceFrac32SMapVSlider) super.getModel();
    }

    /*
     *  Preset logic
     */
    @Override
    public void ShowPreset(int i) {
        this.presetEditActive = i;
        if (i > 0) {
            Preset p = getModel().getPreset(presetEditActive);
            if (p != null) {
                setBackground(Theme.getCurrentTheme().Parameter_Preset_Highlight);
                ctrl.setValue((Double)p.getValue());
            } else {
                setBackground(Theme.getCurrentTheme().Parameter_Default_Background);
                ctrl.setValue(getModel().getValue());
            }
        } else {
            setBackground(Theme.getCurrentTheme().Parameter_Default_Background);
            ctrl.setValue(getModel().getValue());
        }
        if ((getModel().getPresets() != null) && (!getModel().getPresets().isEmpty())) {
//            lblPreset.setVisible(true);
        } else {
//            lblPreset.setVisible(false);
        }
    }

    @Override
    public VSliderComponent CreateControl() {
        return new VSliderComponent(0.0, getModel().getMin(), getModel().getMax(), getModel().getTick());
    }

    @Override
    public VSliderComponent getControlComponent() {
        return (VSliderComponent) ctrl;
    }
}
