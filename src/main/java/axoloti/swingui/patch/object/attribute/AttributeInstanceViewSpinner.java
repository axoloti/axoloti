package axoloti.swingui.patch.object.attribute;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.attribute.AttributeInstanceController;
import axoloti.patch.object.attribute.AttributeInstanceSpinner;
import axoloti.swingui.components.control.ACtrlComponent;
import axoloti.swingui.components.control.NumberBoxComponent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

class AttributeInstanceViewSpinner extends AttributeInstanceViewInt {

    NumberBoxComponent spinner;

    AttributeInstanceViewSpinner(AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public AttributeInstanceSpinner getModel() {
        return (AttributeInstanceSpinner) super.getModel();
    }

    @Override
    void PostConstructor() {
        super.PostConstructor();
        Integer ival = getModel().getValueInteger();
        int value = ival;

        if (value < getModel().getModel().getMinValue()) {
            getModel().setValue(getModel().getModel().getMinValue());
        }
        if (value > getModel().getModel().getMaxValue()) {
            getModel().setValue(getModel().getModel().getMaxValue());
        }
        spinner = new NumberBoxComponent(value, getModel().getModel().getMinValue(), getModel().getModel().getMaxValue(), 1.0);
        add(spinner);
        spinner.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ACtrlComponent.PROP_VALUE_ADJ_BEGIN)) {
                    getController().addMetaUndo("edit attribute " + getModel().getName(), focusEdit);
                } else if (evt.getPropertyName().equals(ACtrlComponent.PROP_VALUE)) {
                    getController().changeValue((Integer) (int) spinner.getValue());
                }
            }
        });
    }

    @Override
    public void Lock() {
        if (spinner != null) {
            spinner.setEnabled(false);
        }
    }

    @Override
    public void UnLock() {
        if (spinner != null) {
            spinner.setEnabled(true);
        }
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (AttributeInstanceSpinner.ATTR_VALUE.is(evt)) {
            Integer newValue = (Integer) evt.getNewValue();
            if (newValue != null) {
                spinner.setValue(newValue);
            }
        } else if (AttributeInstanceSpinner.MAXVALUE.is(evt)) {
            Integer newValue = (Integer) evt.getNewValue();
            if (newValue != null) {
                spinner.setMax(newValue);
            }
        } else if (AttributeInstanceSpinner.MINVALUE.is(evt)) {
            Integer newValue = (Integer) evt.getNewValue();
            if (newValue != null) {
                spinner.setMin(newValue);
            }
        }
    }

}
