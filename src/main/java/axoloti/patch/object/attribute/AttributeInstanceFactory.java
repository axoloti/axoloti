package axoloti.patch.object.attribute;

import axoloti.object.atom.AtomDefinitionController;
import axoloti.object.attribute.AxoAttribute;
import axoloti.object.attribute.AxoAttributeComboBox;
import axoloti.object.attribute.AxoAttributeInt32;
import axoloti.object.attribute.AxoAttributeObjRef;
import axoloti.object.attribute.AxoAttributeSDFile;
import axoloti.object.attribute.AxoAttributeSpinner;
import axoloti.object.attribute.AxoAttributeTablename;
import axoloti.object.attribute.AxoAttributeTextEditor;
import axoloti.patch.object.AxoObjectInstance;

/**
 *
 * @author jtaelman
 */
public class AttributeInstanceFactory {

    private AttributeInstanceFactory() {
    }

    public static AttributeInstance createView(AxoAttribute model, AxoObjectInstance obj) {
        AtomDefinitionController controller = model.getController();
        AttributeInstance view;
        if (model instanceof AxoAttributeComboBox) {
            view = new AttributeInstanceComboBox((AxoAttributeComboBox) model, obj);
        } else if (model instanceof AxoAttributeInt32) {
            view = new AttributeInstanceInt32((AxoAttributeInt32) model, obj);
        } else if (model instanceof AxoAttributeObjRef) {
            view = new AttributeInstanceObjRef((AxoAttributeObjRef) model, obj);
        } else if (model instanceof AxoAttributeSDFile) {
            view = new AttributeInstanceSDFile((AxoAttributeSDFile) model, obj);
        } else if (model instanceof AxoAttributeSpinner) {
            view = new AttributeInstanceSpinner((AxoAttributeSpinner) model, obj);
        } else if (model instanceof AxoAttributeTablename) {
            view = new AttributeInstanceTablename((AxoAttributeTablename) model, obj);
        } else if (model instanceof AxoAttributeTextEditor) {
            view = new AttributeInstanceTextEditor((AxoAttributeTextEditor) model, obj);
        } else {
            view = null;
        }

        controller.addView(view);
        return view;
    }
}
