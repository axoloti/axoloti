package axoloti.attributeviews;

import axoloti.attribute.AttributeInstance;
import axoloti.attribute.AttributeInstanceComboBox;
import axoloti.attribute.AttributeInstanceController;
import axoloti.attribute.AttributeInstanceInt32;
import axoloti.attribute.AttributeInstanceObjRef;
import axoloti.attribute.AttributeInstanceSDFile;
import axoloti.attribute.AttributeInstanceSpinner;
import axoloti.attribute.AttributeInstanceTablename;
import axoloti.attribute.AttributeInstanceTextEditor;
import axoloti.objectviews.AxoObjectInstanceView;
import axoloti.objectviews.IAxoObjectInstanceView;

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
        view.PostConstructor();
        controller.addView(view);
        return view;
    }
}
