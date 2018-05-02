package axoloti.codegen.patch.object.parameter;

import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstance4LevelX16;
import axoloti.patch.object.parameter.ParameterInstanceBin1;
import axoloti.patch.object.parameter.ParameterInstanceBin12;
import axoloti.patch.object.parameter.ParameterInstanceBin16;
import axoloti.patch.object.parameter.ParameterInstanceBin1Momentary;
import axoloti.patch.object.parameter.ParameterInstanceBin32;
import axoloti.patch.object.parameter.ParameterInstanceController;
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

    public static ParameterInstanceView createView(ParameterInstanceController controller) {
        ParameterInstance model = controller.getModel();
        ParameterInstanceView view;
        // order of tests is important!
        if (model instanceof ParameterInstanceBin1Momentary) {
            view = new ParameterInstanceViewBin1Momentary(controller);
        } else if (model instanceof ParameterInstanceBin1) {
            view = new ParameterInstanceViewBin1(controller);
        } else if (model instanceof ParameterInstanceFrac32SMapVSlider) {
            view = new ParameterInstanceViewFrac32S(controller);
        } else if (model instanceof ParameterInstanceFrac32UMapVSlider) {
            view = new ParameterInstanceViewFrac32U(controller);
        } else if (model instanceof ParameterInstanceFrac32UMap) {
            view = new ParameterInstanceViewFrac32U(controller);
        } else if (model instanceof ParameterInstanceFrac32SMap) {
            view = new ParameterInstanceViewFrac32S(controller);
        } else if (model instanceof ParameterInstanceInt32) {
            view = new ParameterInstanceViewInt32(controller);
        } else if (model instanceof ParameterInstanceBin12) {
            view = new ParameterInstanceViewBinN(controller);
        } else if (model instanceof ParameterInstanceBin16) {
            view = new ParameterInstanceViewBinN(controller);
        } else if (model instanceof ParameterInstanceBin32) {
            view = new ParameterInstanceViewBinN(controller);
        } else if (model instanceof ParameterInstance4LevelX16) {
            view = new ParameterInstanceView4LevelX16(controller);
        } else {
            throw new Error("ParameterInstanceViewFactory class not handled: " + model.getClass());
        }
        controller.addView(view);
        return view;
    }
}
