package axoloti.piccolo.parameterviews;

import axoloti.Preset;
import axoloti.Theme;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceFrac32UMapVSlider;
import components.piccolo.control.PVSliderComponent;

public class PParameterInstanceViewFrac32UMapVSlider extends PParameterInstanceViewFrac32U {

    public PParameterInstanceViewFrac32UMapVSlider(ParameterInstanceFrac32UMapVSlider parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
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
                setPaint(Theme.getCurrentTheme().Parameter_Preset_Highlight);
                ctrl.setValue(p.value.getDouble());
            } else {
                setPaint(Theme.getCurrentTheme().Parameter_Default_Background);
                ctrl.setValue(parameterInstance.getValue().getDouble());
            }
        } else {
            setPaint(Theme.getCurrentTheme().Parameter_Default_Background);
            ctrl.setValue(parameterInstance.getValue().getDouble());
        }
        if ((parameterInstance.getPresets() != null) && (!parameterInstance.getPresets().isEmpty())) {
//            lblPreset.setVisible(true);
        } else {
//            lblPreset.setVisible(false);
        }
    }

    @Override
    public PVSliderComponent CreateControl() {
        return new PVSliderComponent(0.0, 0.0, 64, 0.5, axoObjectInstanceView);
    }

    @Override
    public PVSliderComponent getControlComponent() {
        return (PVSliderComponent) ctrl;
    }
}
