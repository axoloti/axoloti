package axoloti.piccolo.patch.object.attribute;

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
import axoloti.abstractui.IAxoObjectInstanceView;

public class PAttributeInstanceViewFactory {

    public static PAttributeInstanceView createView(AttributeInstanceController controller, IAxoObjectInstanceView obj) {
        AttributeInstance model = controller.getModel();
        PAttributeInstanceView view;
        if (model instanceof AttributeInstanceComboBox) {
            view = new PAttributeInstanceViewComboBox(controller, obj);
        } else if (model instanceof AttributeInstanceInt32) {
            view = new PAttributeInstanceViewInt32(controller, obj);
        } else if (model instanceof AttributeInstanceObjRef) {
            view = new PAttributeInstanceViewObjRef(controller, obj);
        } else if (model instanceof AttributeInstanceSDFile) {
            view = new PAttributeInstanceViewSDFile(controller, obj);
        } else if (model instanceof AttributeInstanceSpinner) {
            view = new PAttributeInstanceViewSpinner(controller, obj);
        } else if (model instanceof AttributeInstanceTablename) {
            view = new PAttributeInstanceViewTablename(controller, obj);
        } else if (model instanceof AttributeInstanceTextEditor) {
            view = new PAttributeInstanceViewTextEditor(controller, obj);
        } else {
            view = null;
            throw new Error("unkonwn attribute type");
        }

        view.PostConstructor();
        controller.addView(view);
        return view;
    }
}
