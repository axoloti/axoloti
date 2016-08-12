package axoloti.parameterviews;

import axoloti.Preset;
import axoloti.Theme;
import axoloti.parameters.ParameterInstanceFrac32SMapVSlider;
import components.control.VSliderComponent;

public class ParameterInstanceViewFrac32SMapVSlider extends ParameterInstanceViewFrac32S {

    public ParameterInstanceViewFrac32SMapVSlider(ParameterInstanceFrac32SMapVSlider parameterInstance) {
        super(parameterInstance);
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
    }

    @Override
    public ParameterInstanceFrac32SMapVSlider getParameterInstance() {
        return (ParameterInstanceFrac32SMapVSlider) this.parameterInstance;
    }

    @Override
    public void updateV() {
        if (ctrl != null) {
            ctrl.setValue(getParameterInstance().getValue().getDouble());
        }
    }

    /*
     *  Preset logic
     */
    @Override
    public void ShowPreset(int i) {
        this.presetEditActive = i;
        if (i > 0) {
            Preset p = getParameterInstance().GetPreset(presetEditActive);
            if (p != null) {
                setBackground(Theme.getCurrentTheme().Paramete_Preset_Highlight);
                ctrl.setValue(p.value.getDouble());
            } else {
                setBackground(Theme.getCurrentTheme().Parameter_Default_Background);
                ctrl.setValue(getParameterInstance().getValue().getDouble());
            }
        } else {
            setBackground(Theme.getCurrentTheme().Parameter_Default_Background);
            ctrl.setValue(getParameterInstance().getValue().getDouble());
        }
        if ((getParameterInstance().getPresets() != null) && (!getParameterInstance().getPresets().isEmpty())) {
//            lblPreset.setVisible(true);
        } else {
//            lblPreset.setVisible(false);
        }
    }

    @Override
    public VSliderComponent CreateControl() {
        return new VSliderComponent(0.0, getParameterInstance().getMin(), getParameterInstance().getMax(), getParameterInstance().getTick());
    }

    @Override
    public VSliderComponent getControlComponent() {
        return (VSliderComponent) ctrl;
    }
}
