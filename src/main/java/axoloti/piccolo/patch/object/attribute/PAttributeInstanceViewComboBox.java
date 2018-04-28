package axoloti.piccolo.patch.object.attribute;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.object.attribute.AxoAttributeComboBox;
import axoloti.patch.object.attribute.AttributeInstanceComboBox;
import axoloti.patch.object.attribute.AttributeInstanceController;
import axoloti.piccolo.components.control.PDropDownComponent;
import java.beans.PropertyChangeEvent;
import java.util.List;

public class PAttributeInstanceViewComboBox extends PAttributeInstanceViewString {

    PDropDownComponent comboBox;

    public PAttributeInstanceViewComboBox(AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public AttributeInstanceComboBox getModel() {
        return (AttributeInstanceComboBox) super.getModel();
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        comboBox = new PDropDownComponent(getModel().getModel().getMenuEntries(), getModel(), axoObjectInstanceView);
        setString(getModel().getValue());
        comboBox.addItemListener(new PDropDownComponent.DDCListener() {
                @Override
                public void SelectionChanged() {
                    getController().addMetaUndo("edit attribute " + getModel().getName());
                    getController().changeValue(comboBox.getSelectedItem());

                }
            });
        addChild(comboBox);
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
