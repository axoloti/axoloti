package axoloti.piccolo.patch.object.attribute;

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

public class PAttributeInstanceViewFactory {

    private PAttributeInstanceViewFactory() {
    }

    public static PAttributeInstanceView createView(AttributeInstance model, IAxoObjectInstanceView obj) {
        AttributeInstanceController controller = model.getController();
        PAttributeInstanceView view;
        if (model instanceof AttributeInstanceComboBox) {
            view = new PAttributeInstanceViewComboBox(model, obj);
        } else if (model instanceof AttributeInstanceInt32) {
            view = new PAttributeInstanceViewInt32(model, obj);
        } else if (model instanceof AttributeInstanceObjRef) {
            view = new PAttributeInstanceViewObjRef(model, obj);
        } else if (model instanceof AttributeInstanceSDFile) {
            view = new PAttributeInstanceViewSDFile(model, obj);
        } else if (model instanceof AttributeInstanceSpinner) {
            view = new PAttributeInstanceViewSpinner(model, obj);
        } else if (model instanceof AttributeInstanceTablename) {
            view = new PAttributeInstanceViewTablename(model, obj);
        } else if (model instanceof AttributeInstanceTextEditor) {
            view = new PAttributeInstanceViewTextEditor(model, obj);
        } else {
            view = null;
            throw new Error("unkonwn attribute type");
        }
        controller.addView(view);
        return view;
    }
}
