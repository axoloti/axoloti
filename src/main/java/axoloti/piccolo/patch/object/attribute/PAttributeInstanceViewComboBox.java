package axoloti.piccolo.patch.object.attribute;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.object.attribute.AxoAttributeComboBox;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.attribute.AttributeInstanceComboBox;
import axoloti.piccolo.components.control.PDropDownComponent;
import java.beans.PropertyChangeEvent;
import java.util.List;

class PAttributeInstanceViewComboBox extends PAttributeInstanceViewString {

    PDropDownComponent comboBox;

    PAttributeInstanceViewComboBox(AttributeInstance attribute, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attribute, axoObjectInstanceView);
        initComponents();
    }

    @Override
    public AttributeInstanceComboBox getDModel() {
        return (AttributeInstanceComboBox) super.getDModel();
    }

    private void initComponents() {
        comboBox = new PDropDownComponent(getDModel().getDModel().getMenuEntries(), getDModel(), axoObjectInstanceView);
        setString(getDModel().getValue());
        comboBox.addItemListener(new PDropDownComponent.DDCListener() {
                @Override
                public void selectionChanged() {
                    attribute.getController().addMetaUndo("edit attribute " + getDModel().getName());
                    attribute.getController().changeValue(comboBox.getSelectedItem());
                }
            });
        addChild(comboBox);
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
        AttributeInstanceComboBox aic = (AttributeInstanceComboBox) getDModel();
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
