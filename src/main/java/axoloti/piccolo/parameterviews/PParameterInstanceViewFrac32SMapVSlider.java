package axoloti.piccolo.parameterviews;

import axoloti.preset.PresetDouble;
import axoloti.preferences.Theme;
import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstanceFrac32SMapVSlider;
import axoloti.piccolo.components.control.PVSliderComponent;

public class PParameterInstanceViewFrac32SMapVSlider extends PParameterInstanceViewFrac32S {

    public PParameterInstanceViewFrac32SMapVSlider(ParameterInstanceFrac32SMapVSlider parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
    }

    @Override
    public ParameterInstanceFrac32SMapVSlider getModel() {
        return (ParameterInstanceFrac32SMapVSlider) parameterInstance;
    }

    @Override
    public void updateV() {
        if (ctrl != null) {
            ctrl.setValue(getModel().getValue());
        }
    }

    /*
     *  Preset logic
     */
    @Override
    public void ShowPreset(int i) {
        this.presetEditActive = i;
        if (i > 0) {
            PresetDouble p = getModel().getPreset(presetEditActive);
            if (p != null) {
                setPaint(Theme.getCurrentTheme().Parameter_Preset_Highlight);
                ctrl.setValue(p.getValue());
            } else {
                setPaint(Theme.getCurrentTheme().Parameter_Default_Background);
                ctrl.setValue(getModel().getValue());
            }
        } else {
            setPaint(Theme.getCurrentTheme().Parameter_Default_Background);
            ctrl.setValue(getModel().getValue());
        }
        if ((getModel().getPresets() != null) && (!getModel().getPresets().isEmpty())) {
//            lblPreset.setVisible(true);
        } else {
//            lblPreset.setVisible(false);
        }
    }

    @Override
    public PVSliderComponent CreateControl() {
        return new PVSliderComponent(0.0, getModel().getMin(),
                getModel().getMax(), getModel().getTick(), axoObjectInstanceView);
    }

    @Override
    public PVSliderComponent getControlComponent() {
        return (PVSliderComponent) ctrl;
    }
}
