package axoloti.attributeviews;

import axoloti.attribute.AttributeInstanceComboBox;
import axoloti.attribute.AttributeInstanceController;
import axoloti.objectviews.AxoObjectInstanceView;
import axoloti.utils.Constants;
import components.DropDownComponent;

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
        comboBox = new DropDownComponent(getAttributeInstance().getModel().getMenuEntries(), getAttributeInstance());
        comboBox.setFont(Constants.FONT);
        setString(getAttributeInstance().getValue());
        comboBox.addItemListener(new DropDownComponent.DDCListener() {
            @Override
            public void SelectionChanged() {
                getController().changeValue(comboBox.getSelectedItem());
            }
        });
        add(comboBox);
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

    @Override
    public void setString(String s) {
        AttributeInstanceComboBox aic = (AttributeInstanceComboBox) getController().getModel();
        int index = aic.getIndex(s);
        if (aic.getModel().getMenuEntries().size() > 0) {
            comboBox.setSelectedItem(aic.getModel().getMenuEntries().get(index));
        }
    }
}
