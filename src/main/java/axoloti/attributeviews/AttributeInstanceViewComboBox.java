package axoloti.attributeviews;

import axoloti.attribute.AttributeInstanceComboBox;
import axoloti.attribute.AttributeInstanceController;
import axoloti.object.AxoObjectInstance;
import axoloti.objectviews.AxoObjectInstanceView;
import axoloti.utils.Constants;
import components.DropDownComponent;
import java.util.logging.Level;

class AttributeInstanceViewComboBox extends AttributeInstanceViewString {

    DropDownComponent comboBox;

    public AttributeInstanceViewComboBox(AttributeInstanceComboBox attributeInstance, AttributeInstanceController controller, AxoObjectInstanceView axoObjectView) {
        super(attributeInstance, controller, axoObjectView);
    }

    @Override
    public AttributeInstanceComboBox getAttributeInstance() {
        return (AttributeInstanceComboBox) super.getAttributeInstance();
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        comboBox = new DropDownComponent(getAttributeInstance().getDefinition().getMenuEntries(), getAttributeInstance());
        comboBox.setFont(Constants.FONT);
        setString(getAttributeInstance().getString());
        comboBox.addItemListener(new DropDownComponent.DDCListener() {
            @Override
            public void SelectionChanged() {
                if (!getAttributeInstance().getString().equals(comboBox.getSelectedItem())) {
                    getAttributeInstance().setString(comboBox.getSelectedItem());
                    getAttributeInstance().setSelectedIndex(comboBox.getSelectedIndex());
                    attributeInstance.getObjectInstance().getPatchModel().setDirty();
                }
            }
        });
        add(comboBox);
    }

    @Override
    public String getString() {
        return comboBox.getSelectedItem();
    }

    @Override
    public void setString(String selection) {
        getAttributeInstance().setString(selection);

        if (comboBox == null) {
            return;
        }
        if (comboBox.getItemCount() == 0) {
            return;
        }
        if (selection == null) {
            getAttributeInstance().setString(comboBox.getItemAt(0));
        }
        comboBox.setSelectedItem(getAttributeInstance().getString());
        getAttributeInstance().setSelectedIndex(comboBox.getSelectedIndex());
        if (getAttributeInstance().getString().equals(comboBox.getSelectedItem())) {
            return;
        }
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (getAttributeInstance().getString().equals(comboBox.getItemAt(i))) {
                getAttributeInstance().setString(comboBox.getItemAt(i));
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
