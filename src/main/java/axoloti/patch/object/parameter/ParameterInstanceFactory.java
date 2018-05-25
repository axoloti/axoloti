package axoloti.patch.object.parameter;

import axoloti.object.parameter.*;
import axoloti.patch.object.AxoObjectInstance;
/**
 *
 * @author jtaelman
 */
public class ParameterInstanceFactory {

    private ParameterInstanceFactory() {
    }

    public static ParameterInstance createView(Parameter parameter, AxoObjectInstance obj) {
        ParameterInstance view = null;
        if (parameter instanceof Parameter4LevelX16) {
            view = new ParameterInstance4LevelX16((Parameter4LevelX16) parameter, obj);
        } else if (parameter instanceof ParameterBin1Momentary) {
            view = new ParameterInstanceBin1Momentary((ParameterBin1Momentary) parameter, obj);
        } else if (parameter instanceof ParameterBin1) {
            view = new ParameterInstanceBin1((ParameterBin1) parameter, obj);
        } else if (parameter instanceof ParameterBin12) {
            view = new ParameterInstanceBin12((ParameterBin12) parameter, obj);
        } else if (parameter instanceof ParameterBin16) {
            view = new ParameterInstanceBin16((ParameterBin16) parameter, obj);
        } else if (parameter instanceof ParameterBin32) {
            view = new ParameterInstanceBin32((ParameterBin32) parameter, obj);
        } else if (parameter instanceof ParameterInt32BoxSmall) {
            view = new ParameterInstanceInt32BoxSmall((ParameterInt32BoxSmall) parameter, obj);
        } else if (parameter instanceof ParameterInt32Box) {
            view = new ParameterInstanceInt32Box((ParameterInt32Box) parameter, obj);
        } else if (parameter instanceof ParameterInt32HRadio) {
            view = new ParameterInstanceInt32HRadio((ParameterInt32HRadio) parameter, obj);
        } else if (parameter instanceof ParameterInt32VRadio) {
            view = new ParameterInstanceInt32VRadio((ParameterInt32VRadio) parameter, obj);
        } else if (parameter instanceof ParameterFrac32SMapVSlider) {
            view = new ParameterInstanceFrac32SMapVSlider((ParameterFrac32SMapVSlider) parameter, obj);
        } else if (parameter instanceof ParameterFrac32SMap) {
            view = new ParameterInstanceFrac32SMap((ParameterFrac32SMap) parameter, obj);
        } else if (parameter instanceof ParameterFrac32UMap) {
            view = new ParameterInstanceFrac32UMap((ParameterFrac32UMap) parameter, obj);
        } else if (parameter instanceof ParameterFrac32UMapVSlider) {
            view = new ParameterInstanceFrac32UMapVSlider((ParameterFrac32UMapVSlider) parameter, obj);
        } else {
            throw new Error("unhandled paramter model " + parameter.getClass());
        }
        view.getController().applyDefaultValue();
        parameter.getController().addView(view);
        return view;
    }
}
