package axoloti.swingui.patch.object.attribute;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.attribute.AttributeInstanceComboBox;
import axoloti.patch.object.attribute.AttributeInstanceController;
import axoloti.patch.object.attribute.AttributeInstanceInt32;
import axoloti.patch.object.attribute.AttributeInstanceObjRef;
import axoloti.patch.object.attribute.AttributeInstanceSDFile;
import axoloti.patch.object.attribute.AttributeInstanceSpinner;
import axoloti.patch.object.attribute.AttributeInstanceTablename;
import axoloti.patch.object.attribute.AttributeInstanceTextEditor;
import axoloti.swingui.patch.object.AxoObjectInstanceView;

/**
 *
 * @author jtaelman
 */
public class AttributeInstanceViewFactory {

    public static AttributeInstanceView createView(AttributeInstanceController controller, IAxoObjectInstanceView obj) {
        AttributeInstance model = controller.getModel();
        AttributeInstanceView view;
        if (model instanceof AttributeInstanceComboBox) {
            view = new AttributeInstanceViewComboBox(controller, (AxoObjectInstanceView) obj);
        } else if (model instanceof AttributeInstanceInt32) {
            view = new AttributeInstanceViewInt32(controller, obj);
        } else if (model instanceof AttributeInstanceObjRef) {
            view = new AttributeInstanceViewObjRef(controller, obj);
        } else if (model instanceof AttributeInstanceSDFile) {
            view = new AttributeInstanceViewSDFile(controller, obj);
        } else if (model instanceof AttributeInstanceSpinner) {
            view = new AttributeInstanceViewSpinner(controller, obj);
        } else if (model instanceof AttributeInstanceTablename) {
            view = new AttributeInstanceViewTablename(controller, obj);
        } else if (model instanceof AttributeInstanceTextEditor) {
            view = new AttributeInstanceViewTextEditor(controller, obj);
        } else {
            view = null;
            throw new Error("unkonwn attribute type");
        }
        /*
         // these have different constructors... FIXME
         } else if (model instanceof AttributeInstanceWavefile) {
         return new AttributeInstanceWavefile((AttributeInstanceWavefile)model, obj);
         }
         */
        controller.addView(view);
        return view;
    }
}
