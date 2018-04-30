package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
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
import axoloti.patch.object.parameter.ParameterInstanceInt32Box;
import axoloti.patch.object.parameter.ParameterInstanceInt32BoxSmall;
import axoloti.patch.object.parameter.ParameterInstanceInt32HRadio;
import axoloti.patch.object.parameter.ParameterInstanceInt32VRadio;

/**
 *
 * @author jtaelman
 */
public class ParameterInstanceViewFactory {

    public static ParameterInstanceView createView(ParameterInstanceController controller, IAxoObjectInstanceView obj) {
        ParameterInstance model = controller.getModel();
        ParameterInstanceView view;
        if (model instanceof ParameterInstance4LevelX16) {
            view = new ParameterInstanceView4LevelX16(controller, obj);
        } else if (model instanceof ParameterInstanceBin1) {
            view = new ParameterInstanceViewBin1(controller, obj);
        } else if (model instanceof ParameterInstanceBin12) {
            view = new ParameterInstanceViewBin12(controller, obj);
        } else if (model instanceof ParameterInstanceBin16) {
            view = new ParameterInstanceViewBin16(controller, obj);
        } else if (model instanceof ParameterInstanceBin1Momentary) {
            view = new ParameterInstanceViewBin1Momentary(controller, obj);
        } else if (model instanceof ParameterInstanceBin32) {
            view = new ParameterInstanceViewBin32(controller, obj);
        } else if (model instanceof ParameterInstanceFrac32SMap) {
            view = new ParameterInstanceViewFrac32SMap(controller, obj);
        } else if (model instanceof ParameterInstanceFrac32SMapVSlider) {
            view = new ParameterInstanceViewFrac32SMapVSlider(controller, obj);
        } else if (model instanceof ParameterInstanceFrac32UMap) {
            view = new ParameterInstanceViewFrac32UMap(controller, obj);
        } else if (model instanceof ParameterInstanceFrac32UMapVSlider) {
            view = new ParameterInstanceViewFrac32UMapVSlider(controller, obj);
        } else if (model instanceof ParameterInstanceInt32Box) {
            view = new ParameterInstanceViewInt32Box(controller, obj);
        } else if (model instanceof ParameterInstanceInt32BoxSmall) {
            view = new ParameterInstanceViewInt32BoxSmall(controller, obj);
        } else if (model instanceof ParameterInstanceInt32HRadio) {
            view = new ParameterInstanceViewInt32HRadio(controller, obj);
        } else if (model instanceof ParameterInstanceInt32VRadio) {
            view = new ParameterInstanceViewInt32VRadio(controller, obj);
        } else {
            view = null;
            throw new Error("unknown parameter type");
        }
        controller.addView(view);
        return view;
    }
}
