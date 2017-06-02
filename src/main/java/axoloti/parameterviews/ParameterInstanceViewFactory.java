package axoloti.parameterviews;

import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstance;
import axoloti.parameters.ParameterInstanceBin1;
import axoloti.parameters.ParameterInstanceBin12;
import axoloti.parameters.ParameterInstanceBin16;
import axoloti.parameters.ParameterInstanceBin1Momentary;
import axoloti.parameters.ParameterInstanceController;
import axoloti.parameters.ParameterInstanceFrac32SMap;
import axoloti.parameters.ParameterInstanceFrac32SMapVSlider;
import axoloti.parameters.ParameterInstanceFrac32UMap;
import axoloti.parameters.ParameterInstanceFrac32UMapVSlider;
import axoloti.parameters.ParameterInstanceInt32Box;
import axoloti.parameters.ParameterInstanceInt32BoxSmall;
import axoloti.parameters.ParameterInstanceInt32HRadio;
import axoloti.parameters.ParameterInstanceInt32VRadio;

/**
 *
 * @author jtaelman
 */
public class ParameterInstanceViewFactory {

    public static ParameterInstanceView createView(ParameterInstanceController controller, IAxoObjectInstanceView obj) {
        ParameterInstance model = controller.getModel();
        ParameterInstanceView view;
        if (model instanceof ParameterInstanceBin1) {
            view = new ParameterInstanceViewBin1((ParameterInstanceBin1) model, controller, obj);
        } else if (model instanceof ParameterInstanceBin12) {
            view = new ParameterInstanceViewBin12((ParameterInstanceBin12) model, controller, obj);
        } else if (model instanceof ParameterInstanceBin16) {
            view = new ParameterInstanceViewBin16((ParameterInstanceBin16) model, controller, obj);
        } else if (model instanceof ParameterInstanceBin1Momentary) {
            return new ParameterInstanceViewBin1Momentary((ParameterInstanceBin1Momentary) model, controller, obj);
        } else if (model instanceof ParameterInstanceFrac32SMap) {
            view = new ParameterInstanceViewFrac32SMap((ParameterInstanceFrac32SMap) model, controller, obj);
        } else if (model instanceof ParameterInstanceFrac32SMapVSlider) {
            view = new ParameterInstanceViewFrac32SMapVSlider((ParameterInstanceFrac32SMapVSlider) model, controller, obj);
        } else if (model instanceof ParameterInstanceFrac32UMap) {
            view = new ParameterInstanceViewFrac32UMap((ParameterInstanceFrac32UMap) model, controller, obj);
        } else if (model instanceof ParameterInstanceFrac32UMapVSlider) {
            view = new ParameterInstanceViewFrac32UMapVSlider((ParameterInstanceFrac32UMapVSlider) model, controller, obj);
        } else if (model instanceof ParameterInstanceInt32Box) {
            view = new ParameterInstanceViewInt32Box((ParameterInstanceInt32Box) model, controller, obj);
        } else if (model instanceof ParameterInstanceInt32BoxSmall) {
            view = new ParameterInstanceViewInt32BoxSmall((ParameterInstanceInt32BoxSmall) model, controller, obj);
        } else if (model instanceof ParameterInstanceInt32HRadio) {
            view = new ParameterInstanceViewInt32HRadio((ParameterInstanceInt32HRadio) model, controller, obj);
        } else if (model instanceof ParameterInstanceInt32VRadio) {
            view = new ParameterInstanceViewInt32VRadio((ParameterInstanceInt32VRadio) model, controller, obj);
        } else {
            view = null;
        }
        view.PostConstructor();
        controller.addView(view);
        return view;
    }
}
