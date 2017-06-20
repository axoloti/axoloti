package axoloti.attribute;

import axoloti.atom.AtomDefinition;
import axoloti.atom.AtomDefinitionController;
import axoloti.attributedefinition.AxoAttributeComboBox;
import axoloti.attributedefinition.AxoAttributeInt32;
import axoloti.attributedefinition.AxoAttributeObjRef;
import axoloti.attributedefinition.AxoAttributeSDFile;
import axoloti.attributedefinition.AxoAttributeSpinner;
import axoloti.attributedefinition.AxoAttributeTablename;
import axoloti.attributedefinition.AxoAttributeTextEditor;
import axoloti.object.AxoObjectInstance;

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
