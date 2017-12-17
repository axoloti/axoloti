package axoloti.patch.object.attribute;

import axoloti.object.atom.AtomDefinition;
import axoloti.object.atom.AtomDefinitionController;
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

    public static AttributeInstance createView(AtomDefinitionController controller, AxoObjectInstance obj) {
        AtomDefinition model = controller.getModel();
        AttributeInstance view;
        if (model instanceof AxoAttributeComboBox) {
            view = new AttributeInstanceComboBox(controller, obj);
        } else if (model instanceof AxoAttributeInt32) {
            view = new AttributeInstanceInt32(controller, obj);
        } else if (model instanceof AxoAttributeObjRef) {
            view = new AttributeInstanceObjRef(controller, obj);
        } else if (model instanceof AxoAttributeSDFile) {
            view = new AttributeInstanceSDFile(controller, obj);
        } else if (model instanceof AxoAttributeSpinner) {
            view = new AttributeInstanceSpinner(controller, obj);
        } else if (model instanceof AxoAttributeTablename) {
            view = new AttributeInstanceTablename(controller, obj);
        } else if (model instanceof AxoAttributeTextEditor) {
            view = new AttributeInstanceTextEditor(controller, obj);
        } else {
            view = null;
        }

        controller.addView(view);
        return view;
    }
}
