package axoloti.swingui.patch.object.attribute;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.attribute.AttributeInstanceSpinner;
import axoloti.swingui.components.control.ACtrlComponent;
import axoloti.swingui.components.control.NumberBoxComponent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

class AttributeInstanceViewSpinner extends AttributeInstanceViewInt {

    private NumberBoxComponent spinner;

    AttributeInstanceViewSpinner(AttributeInstance attribute, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attribute, axoObjectInstanceView);
        initComponents();
    }

    @Override
    public AttributeInstanceSpinner getDModel() {
        return (AttributeInstanceSpinner) super.getDModel();
    }

    private void initComponents() {
        Integer ival = getDModel().getValueInteger();
        int value = ival;

        if (value < getDModel().getDModel().getMinValue()) {
            getDModel().setValue(getDModel().getDModel().getMinValue());
        }
        if (value > getDModel().getDModel().getMaxValue()) {
            getDModel().setValue(getDModel().getDModel().getMaxValue());
        }
        spinner = new NumberBoxComponent(value, getDModel().getDModel().getMinValue(), getDModel().getDModel().getMaxValue(), 1.0);
        add(spinner);
        spinner.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ACtrlComponent.PROP_VALUE_ADJ_BEGIN)) {
                    model.getController().addMetaUndo("edit attribute " + getDModel().getName(), focusEdit);
                } else if (evt.getPropertyName().equals(ACtrlComponent.PROP_VALUE)) {
                    model.getController().changeValue((Integer) (int) spinner.getValue());
                }
            }
        });
    }

    @Override
    public void lock() {
        if (spinner != null) {
            spinner.setEnabled(false);
        }
    }

    @Override
    public void unlock() {
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
