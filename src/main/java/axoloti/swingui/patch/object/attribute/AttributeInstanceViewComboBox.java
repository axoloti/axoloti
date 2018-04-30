package axoloti.swingui.patch.object.attribute;

import axoloti.object.attribute.AxoAttributeComboBox;
import axoloti.patch.object.attribute.AttributeInstanceComboBox;
import axoloti.patch.object.attribute.AttributeInstanceController;
import axoloti.swingui.components.DropDownComponent;
import axoloti.swingui.patch.object.AxoObjectInstanceView;
import axoloti.utils.Constants;
import java.beans.PropertyChangeEvent;
import java.util.List;

class AttributeInstanceViewComboBox extends AttributeInstanceViewString {

    DropDownComponent comboBox;

    public AttributeInstanceViewComboBox(AttributeInstanceController controller, AxoObjectInstanceView axoObjectView) {
        super(controller, axoObjectView);
        initComponents();
    }

    @Override
    public AttributeInstanceComboBox getModel() {
        return (AttributeInstanceComboBox) super.getModel();
    }

    private void initComponents() {
        comboBox = new DropDownComponent(getModel().getModel().getMenuEntries());
        comboBox.setFont(Constants.FONT);
        setString(getModel().getValue());
        comboBox.addItemListener(new DropDownComponent.DDCListener() {
            @Override
            public void SelectionChanged() {
                getController().addMetaUndo("edit attribute " + getModel().getName(), focusEdit);
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

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (AxoAttributeComboBox.ATOM_MENUENTRIES.is(evt)) {
            comboBox.setItems((List<String>) evt.getNewValue());
        }
    }

}
