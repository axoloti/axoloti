package axoloti.swingui.patch.object.attribute;

import axoloti.object.attribute.AxoAttributeComboBox;
import axoloti.patch.object.attribute.AttributeInstanceComboBox;
import axoloti.swingui.components.DropDownComponent;
import axoloti.swingui.patch.object.AxoObjectInstanceView;
import axoloti.utils.Constants;
import java.beans.PropertyChangeEvent;
import java.util.List;

class AttributeInstanceViewComboBox extends AttributeInstanceViewString {

    private DropDownComponent comboBox;

    AttributeInstanceViewComboBox(AttributeInstanceComboBox attribute, AxoObjectInstanceView axoObjectView) {
        super(attribute, axoObjectView);
        initComponents();
    }

    @Override
    public AttributeInstanceComboBox getDModel() {
        return (AttributeInstanceComboBox) super.getDModel();
    }

    private void initComponents() {
        comboBox = new DropDownComponent(getDModel().getDModel().getMenuEntries());
        comboBox.setFont(Constants.FONT);
        setString(getDModel().getValue());
        comboBox.addItemListener(new DropDownComponent.DDCListener() {
            @Override
            public void selectionChanged() {
                model.getController().addMetaUndo("edit attribute " + getDModel().getName(), focusEdit);
                model.getController().changeValue(comboBox.getSelectedItem());
            }
        });
        add(comboBox);
    }

    @Override
    public void lock() {
        if (comboBox != null) {
            comboBox.setEnabled(false);
        }
    }

    @Override
    public void unlock() {
        if (comboBox != null) {
            comboBox.setEnabled(true);
        }
    }

    @Override
    public void setString(String s) {
        AttributeInstanceComboBox aic = getDModel();
        int index = aic.getIndex(s);
        if (aic.getDModel().getMenuEntries().size() > 0) {
            comboBox.setSelectedItem(aic.getDModel().getMenuEntries().get(index));
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
