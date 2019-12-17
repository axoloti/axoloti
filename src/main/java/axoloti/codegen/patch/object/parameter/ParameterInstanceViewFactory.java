package axoloti.codegen.patch.object.parameter;

import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstance4LevelX16;
import axoloti.patch.object.parameter.ParameterInstanceBin1;
import axoloti.patch.object.parameter.ParameterInstanceBin12;
import axoloti.patch.object.parameter.ParameterInstanceBin16;
import axoloti.patch.object.parameter.ParameterInstanceBin1Momentary;
import axoloti.patch.object.parameter.ParameterInstanceBin32;
import axoloti.patch.object.parameter.ParameterInstanceFrac32SMap;
import axoloti.patch.object.parameter.ParameterInstanceFrac32SMapVSlider;
import axoloti.patch.object.parameter.ParameterInstanceFrac32UMap;
import axoloti.patch.object.parameter.ParameterInstanceFrac32UMapVSlider;
import axoloti.patch.object.parameter.ParameterInstanceInt32;

/**
 *
 * @author jtaelman
 */
public class ParameterInstanceViewFactory {

    private ParameterInstanceViewFactory() {
    }

    public static ParameterInstanceView createView(ParameterInstance model) {
        ParameterInstanceView view;
        // order of tests is important!
        if (model instanceof ParameterInstanceBin1Momentary) {
            view = new ParameterInstanceViewBin1Momentary(model);
        } else if (model instanceof ParameterInstanceBin1) {
            view = new ParameterInstanceViewBin1(model);
        } else if (model instanceof ParameterInstanceFrac32SMapVSlider) {
            view = new ParameterInstanceViewFrac32S(model);
        } else if (model instanceof ParameterInstanceFrac32UMapVSlider) {
            view = new ParameterInstanceViewFrac32U(model);
        } else if (model instanceof ParameterInstanceFrac32SMap) {
            view = new ParameterInstanceViewFrac32S(model);
        } else if (model instanceof ParameterInstanceFrac32UMap) {
            view = new ParameterInstanceViewFrac32U(model);
        } else if (model instanceof ParameterInstanceInt32) {
            view = new ParameterInstanceViewInt32(model);
        } else if (model instanceof ParameterInstanceBin12) {
            view = new ParameterInstanceViewBinN(model);
        } else if (model instanceof ParameterInstanceBin16) {
            view = new ParameterInstanceViewBinN(model);
        } else if (model instanceof ParameterInstanceBin32) {
            view = new ParameterInstanceViewBinN(model);
        } else if (model instanceof ParameterInstance4LevelX16) {
            view = new ParameterInstanceView4LevelX16(model);
        } else {
            throw new Error("ParameterInstanceViewFactory class not handled: " + model.getClass());
        }
        model.getController().addView(view);
        return view;
    }
}
