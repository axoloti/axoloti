package axoloti.piccolo.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.preset.PresetDouble;
import axoloti.piccolo.components.control.PVSliderComponent;
import axoloti.preferences.Theme;

class PParameterInstanceViewFrac32UMapVSlider extends PParameterInstanceViewFrac32U {

    public PParameterInstanceViewFrac32UMapVSlider(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
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
//        if ((parameterInstance.getPresets() != null) && (!parameterInstance.getPresets().isEmpty())) {
//            lblPreset.setVisible(true);
//        } else {
//            lblPreset.setVisible(false);
//        }
    }

    @Override
    public PVSliderComponent createControl() {
        return new PVSliderComponent(0.0, 0.0, 64, 0.5, axoObjectInstanceView);
    }

    @Override
    public PVSliderComponent getControlComponent() {
        return (PVSliderComponent) ctrl;
    }
}
