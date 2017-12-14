package axoloti.attributeviews;

import axoloti.atom.AtomDefinitionController;
import axoloti.attribute.AttributeInstanceComboBox;
import axoloti.attribute.AttributeInstanceController;
import axoloti.attributedefinition.AxoAttributeComboBox;
import axoloti.objectviews.AxoObjectInstanceView;
import axoloti.utils.Constants;
import components.DropDownComponent;
import java.beans.PropertyChangeEvent;
import java.util.List;

class AttributeInstanceViewComboBox extends AttributeInstanceViewString {

    DropDownComponent comboBox;

    public AttributeInstanceViewComboBox(AttributeInstanceController controller, AxoObjectInstanceView axoObjectView) {
        super(controller, axoObjectView);
    }

    @Override
    public AttributeInstanceComboBox getModel() {
        return (AttributeInstanceComboBox) super.getModel();
    }

    @Override
    void PostConstructor() {
        super.PostConstructor();
        comboBox = new DropDownComponent(getModel().getModel().getMenuEntries());
        comboBox.setFont(Constants.FONT);
        setString(getModel().getValue());
        comboBox.addItemListener(new DropDownComponent.DDCListener() {
            @Override
            public void SelectionChanged() {
                getController().addMetaUndo("edit attribute " + getModel().getName());
                getController().setModelUndoableProperty(AttributeInstanceComboBox.ATTR_VALUE,comboBox.getSelectedItem());
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

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (AxoAttributeComboBox.ATOM_MENUENTRIES.is(evt)) {
            comboBox.setItems((List<String>) evt.getNewValue());
        }
    }

}
