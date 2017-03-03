package axoloti.piccolo.attributeviews;

import axoloti.attribute.AttributeInstanceComboBox;
import axoloti.object.AxoObjectInstance;
import axoloti.objectviews.IAxoObjectInstanceView;
import components.piccolo.control.PDropDownComponent;
import java.util.logging.Level;

public class PAttributeInstanceViewComboBox extends PAttributeInstanceViewString {

    PDropDownComponent comboBox;
    AttributeInstanceComboBox attributeInstance;

    public PAttributeInstanceViewComboBox(AttributeInstanceComboBox attributeInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attributeInstance, axoObjectInstanceView);
        this.attributeInstance = attributeInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        comboBox = new PDropDownComponent(attributeInstance.getDefinition().getMenuEntries(), attributeInstance, axoObjectInstanceView);
        setString(attributeInstance.getString());
        comboBox.addItemListener(new PDropDownComponent.DDCListener() {
            @Override
            public void SelectionChanged() {
                if (!attributeInstance.getString().equals(comboBox.getSelectedItem())) {
                    attributeInstance.setString(comboBox.getSelectedItem());
                    attributeInstance.setSelectedIndex(comboBox.getSelectedIndex());
                    axoObjectInstanceView.getPatchView().getPatchController().pushUndoState();
                    attributeInstance.getObjectInstance().getPatchModel().setDirty();
                }
            }
        });
        addChild(comboBox);
    }

    @Override
    public String getString() {
        return comboBox.getSelectedItem();
    }

    @Override
    public void setString(String selection) {
        attributeInstance.setString(selection);

        if (comboBox == null) {
            return;
        }
        if (comboBox.getItemCount() == 0) {
            return;
        }
        if (selection == null) {
            attributeInstance.setString(comboBox.getItemAt(0));
        }
        comboBox.setSelectedItem(attributeInstance.getString());
        attributeInstance.setSelectedIndex(comboBox.getSelectedIndex());
        if (attributeInstance.getString().equals(comboBox.getSelectedItem())) {
            return;
        }
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (attributeInstance.getString().equals(comboBox.getItemAt(i))) {
                attributeInstance.setString(comboBox.getItemAt(i));
                return;
            }
        }
        java.util.logging.Logger.getLogger(AxoObjectInstance.class.getName()).log(Level.SEVERE, "Error: object \"{0}\" attribute \"{1}\", value \"{2}\" unmatched", new Object[]{attributeInstance.getObjectInstance().getInstanceName(), attributeInstance.getDefinition().getName(), selection});
    }

    @Override
    public void Lock() {
        if (comboBox != null) {
            comboBox.setEnabled(false);
        }
    }

    @Override
    public void UnLock() {
        if (comboBox != null) {
            comboBox.setEnabled(true);
        }
    }
}
