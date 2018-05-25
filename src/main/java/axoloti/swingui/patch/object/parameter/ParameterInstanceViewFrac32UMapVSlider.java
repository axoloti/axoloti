package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.preset.Preset;
import axoloti.preferences.Theme;
import axoloti.swingui.components.control.VSliderComponent;

class ParameterInstanceViewFrac32UMapVSlider extends ParameterInstanceViewFrac32U {

    ParameterInstanceViewFrac32UMapVSlider(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
        initCtrlComponent(ctrl);
    }

    /*
     *  Preset logic
     */
    @Override
    public void showPreset(int i) {
        this.presetEditActive = i;
        if (i > 0) {
            Preset p = getDModel().getPreset(presetEditActive);
            if (p != null) {
                setBackground(Theme.getCurrentTheme().Parameter_Preset_Highlight);
                ctrl.setValue((Double)p.getValue());
            } else {
                setBackground(Theme.getCurrentTheme().Parameter_Default_Background);
                ctrl.setValue(getDModel().getValue());
            }
        } else {
            setBackground(Theme.getCurrentTheme().Parameter_Default_Background);
            ctrl.setValue(getDModel().getValue());
        }
        if ((getDModel().getPresets() != null) && (!getDModel().getPresets().isEmpty())) {
//            lblPreset.setVisible(true);
        } else {
//            lblPreset.setVisible(false);
        }
    }

    @Override
    public VSliderComponent createControl() {
        return new VSliderComponent(0.0, 0.0, 64, 0.5);
    }

    private final VSliderComponent ctrl = createControl();

    @Override
    public VSliderComponent getControlComponent() {
        return ctrl;
    }
}
