package axoloti.piccolo.patch.object.attribute;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.attribute.AttributeInstanceSpinner;
import axoloti.piccolo.components.control.PNumberBoxComponent;
import static axoloti.swingui.components.control.ACtrlComponent.PROP_VALUE;
import static axoloti.swingui.components.control.ACtrlComponent.PROP_VALUE_ADJ_BEGIN;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

class PAttributeInstanceViewSpinner extends PAttributeInstanceViewInt {

    PNumberBoxComponent spinner;

    PAttributeInstanceViewSpinner(AttributeInstance attribute, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attribute, axoObjectInstanceView);
        initComponents();
    }

    @Override
    public AttributeInstanceSpinner getDModel() {
        return (AttributeInstanceSpinner) super.getDModel();
    }

    private void initComponents() {
        int value = getDModel().getValueInteger();

        if (value < getDModel().getDModel().getMinValue()) {
            getDModel().setValue(getDModel().getDModel().getMinValue());
        }
        if (value > getDModel().getDModel().getMaxValue()) {
            getDModel().setValue(getDModel().getDModel().getMaxValue());
        }
        spinner = new PNumberBoxComponent(
            value,
            getDModel().getDModel().getMinValue(),
            getDModel().getDModel().getMaxValue(), 1.0, axoObjectInstanceView);
        addChild(spinner);
        spinner.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals(PROP_VALUE_ADJ_BEGIN)) {
                        getDModel().getController().addMetaUndo("edit attribute " + getDModel().getName());
                    } else if (evt.getPropertyName().equals(PROP_VALUE)) {
                        getDModel().getController().changeValue((Integer) (int) spinner.getValue());
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
            spinner.setValue(newValue);
        } else if (AttributeInstanceSpinner.MAXVALUE.is(evt)) {
            Integer newValue = (Integer) evt.getNewValue();
            spinner.setMax(newValue);
        } else if (AttributeInstanceSpinner.MINVALUE.is(evt)) {
            Integer newValue = (Integer) evt.getNewValue();
            spinner.setMin(newValue);
        }
    }
}
