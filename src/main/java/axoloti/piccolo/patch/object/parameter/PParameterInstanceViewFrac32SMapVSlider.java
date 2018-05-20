package axoloti.piccolo.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstanceFrac32SMapVSlider;
import axoloti.patch.object.parameter.preset.PresetDouble;
import axoloti.piccolo.components.control.PVSliderComponent;
import axoloti.preferences.Theme;

class PParameterInstanceViewFrac32SMapVSlider extends PParameterInstanceViewFrac32S {

    public PParameterInstanceViewFrac32SMapVSlider(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public ParameterInstanceFrac32SMapVSlider getDModel() {
        return (ParameterInstanceFrac32SMapVSlider) super.getDModel();
    }

    /*
     *  Preset logic
     */
    @Override
    public void showPreset(int i) {
        this.presetEditActive = i;
        if (i > 0) {
            PresetDouble p = getDModel().getPreset(presetEditActive);
            if (p != null) {
                setPaint(Theme.getCurrentTheme().Parameter_Preset_Highlight);
                ctrl.setValue(p.getValue());
            } else {
                setPaint(Theme.getCurrentTheme().Parameter_Default_Background);
                ctrl.setValue(getDModel().getValue());
            }
        } else {
            setPaint(Theme.getCurrentTheme().Parameter_Default_Background);
            ctrl.setValue(getDModel().getValue());
        }
        if ((getDModel().getPresets() != null) && (!getDModel().getPresets().isEmpty())) {
//            lblPreset.setVisible(true);
        } else {
//            lblPreset.setVisible(false);
        }
    }

    @Override
    public PVSliderComponent createControl() {
        return new PVSliderComponent(0.0, getDModel().getMin(),
                getDModel().getMax(), getDModel().getTick(), axoObjectInstanceView);
    }

    @Override
    public PVSliderComponent getControlComponent() {
        return (PVSliderComponent) ctrl;
    }
}
