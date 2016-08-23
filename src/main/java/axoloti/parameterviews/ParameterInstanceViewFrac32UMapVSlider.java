package axoloti.parameterviews;

import axoloti.Preset;
import axoloti.Theme;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceFrac32UMapVSlider;
import components.control.VSliderComponent;

public class ParameterInstanceViewFrac32UMapVSlider extends ParameterInstanceViewFrac32U {

    public ParameterInstanceViewFrac32UMapVSlider(ParameterInstanceFrac32UMapVSlider parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
    }

    @Override
    public void updateV() {
        if (ctrl != null) {
            ctrl.setValue(parameterInstance.getValue().getDouble());
        }
    }

    /*
     *  Preset logic
     */
    @Override
    public void ShowPreset(int i) {
        this.presetEditActive = i;
        if (i > 0) {
            Preset p = parameterInstance.GetPreset(presetEditActive);
            if (p != null) {
                setBackground(Theme.getCurrentTheme().Parameter_Preset_Highlight);
                ctrl.setValue(p.value.getDouble());
            } else {
                setBackground(Theme.getCurrentTheme().Parameter_Default_Background);
                ctrl.setValue(parameterInstance.getValue().getDouble());
            }
        } else {
            setBackground(Theme.getCurrentTheme().Parameter_Default_Background);
            ctrl.setValue(parameterInstance.getValue().getDouble());
        }
        if ((parameterInstance.getPresets() != null) && (!parameterInstance.getPresets().isEmpty())) {
//            lblPreset.setVisible(true);
        } else {
//            lblPreset.setVisible(false);
        }
    }

    @Override
    public VSliderComponent CreateControl() {
        return new VSliderComponent(0.0, 0.0, 64, 0.5);
    }

    @Override
    public VSliderComponent getControlComponent() {
        return (VSliderComponent) ctrl;
    }
}
