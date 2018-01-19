package axoloti.piccolo.patch.object.attribute;

import axoloti.patch.object.attribute.AttributeInstanceSpinner;
import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.piccolo.components.control.PNumberBoxComponent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import axoloti.patch.object.attribute.AttributeInstanceController;
import static axoloti.swingui.components.control.ACtrlComponent.PROP_VALUE_ADJ_BEGIN;
import static axoloti.swingui.components.control.ACtrlComponent.PROP_VALUE;

public class PAttributeInstanceViewSpinner extends PAttributeInstanceViewInt {

    PNumberBoxComponent spinner;

    public PAttributeInstanceViewSpinner(AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public AttributeInstanceSpinner getModel() {
        return (AttributeInstanceSpinner) super.getModel();
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        int value = getModel().getValue();

        if (value < getModel().getModel().getMinValue()) {
            getModel().setValue(getModel().getModel().getMinValue());
        }
        if (value > getModel().getModel().getMaxValue()) {
            getModel().setValue(getModel().getModel().getMaxValue());
        }
        spinner = new PNumberBoxComponent(
            value,
            getModel().getModel().getMinValue(),
            getModel().getModel().getMaxValue(), 1.0, axoObjectInstanceView);
        addChild(spinner);
        spinner.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals(PROP_VALUE_ADJ_BEGIN)) {
                        getController().addMetaUndo("edit attribute " + getModel().getName());
                    } else if (evt.getPropertyName().equals(PROP_VALUE)) {
                        controller.setModelUndoableProperty(AttributeInstanceSpinner.ATTR_VALUE,(Integer) (int) spinner.getValue());
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
