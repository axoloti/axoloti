package axoloti.piccolo.attributeviews;

import axoloti.attribute.AttributeInstanceComboBox;
import axoloti.attribute.AttributeInstanceController;
import axoloti.mvc.AbstractController;
import axoloti.object.AxoObjectInstance;
import axoloti.objectviews.IAxoObjectInstanceView;
import components.piccolo.control.PDropDownComponent;
import java.beans.PropertyChangeEvent;
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
        comboBox = new PDropDownComponent(attributeInstance.getModel().getMenuEntries(), attributeInstance, axoObjectInstanceView);
        setString(attributeInstance.getValue());
        comboBox.addItemListener(new PDropDownComponent.DDCListener() {
            @Override
            public void SelectionChanged() {
                if (!attributeInstance.getValue().equals(comboBox.getSelectedItem())) {
                    attributeInstance.setValue(comboBox.getSelectedItem());
                    //attributeInstance.setSelectedIndex(comboBox.getSelectedIndex());
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
        // TODO: use MVC pattern
        /*
        attributeInstance.setValue(selection);

        if (comboBox == null) {
            return;
        }
        if (comboBox.getItemCount() == 0) {
            return;
        }
        if (selection == null) {
            attributeInstance.setValue(comboBox.getItemAt(0));
        }
        comboBox.setSelectedItem(attributeInstance.getValue());
        attributeInstance.setSelectedIndex(comboBox.getSelectedIndex());
        if (attributeInstance.getValue().equals(comboBox.getSelectedItem())) {
            return;
        }
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (attributeInstance.getValue().equals(comboBox.getItemAt(i))) {
                attributeInstance.setValue(comboBox.getItemAt(i));
                return;
            }
        }
        java.util.logging.Logger.getLogger(AxoObjectInstance.class.getName()).log(Level.SEVERE, "Error: object \"{0}\" attribute \"{1}\", value \"{2}\" unmatched", new Object[]{attributeInstance.getObjectInstance().getInstanceName(), attributeInstance.getModel().getName(), selection});
        */
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
