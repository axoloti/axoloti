package axoloti.swingui.patch.object.attribute;

import axoloti.object.attribute.AxoAttributeComboBox;
import axoloti.patch.object.attribute.AttributeInstanceMidiOutPort;
import axoloti.swingui.components.DropDownComponent;
import axoloti.swingui.patch.object.AxoObjectInstanceView;
import axoloti.utils.Constants;
import java.beans.PropertyChangeEvent;
import java.util.List;

class AttributeInstanceViewMidiOutPort extends AttributeInstanceViewString {

    private DropDownComponent comboBox;

    AttributeInstanceViewMidiOutPort(AttributeInstanceMidiOutPort attribute, AxoObjectInstanceView axoObjectView) {
        super(attribute, axoObjectView);
        initComponents();
    }

    @Override
    public AttributeInstanceMidiOutPort getDModel() {
        return (AttributeInstanceMidiOutPort) super.getDModel();
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
        AttributeInstanceMidiOutPort aic = getDModel();
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
