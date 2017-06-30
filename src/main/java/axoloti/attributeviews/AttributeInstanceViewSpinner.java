package axoloti.attributeviews;

import axoloti.atom.AtomDefinitionController;
import axoloti.attribute.AttributeInstanceController;
import axoloti.attribute.AttributeInstanceSpinner;
import axoloti.objectviews.IAxoObjectInstanceView;
import components.control.ACtrlEvent;
import components.control.ACtrlListener;
import components.control.NumberBoxComponent;
import java.beans.PropertyChangeEvent;

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
        Integer ival = getModel().getValue();
        int value = ival;

        if (value < getModel().getModel().getMinValue()) {
            getModel().setValue(getModel().getModel().getMinValue());
        }
        if (value > getModel().getModel().getMaxValue()) {
            getModel().setValue(getModel().getModel().getMaxValue());
        }
        spinner = new NumberBoxComponent(value, getModel().getModel().getMinValue(), getModel().getModel().getMaxValue(), 1.0);
        add(spinner);
        spinner.addACtrlListener(new ACtrlListener() {
            @Override
            public void ACtrlAdjusted(ACtrlEvent e) {
                controller.changeValue((Integer) (int) spinner.getValue());
            }

            @Override
            public void ACtrlAdjustmentBegin(ACtrlEvent e) {
                getController().addMetaUndo("edit attribute " + getModel().getName());
            }

            @Override
            public void ACtrlAdjustmentFinished(ACtrlEvent e) {
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
        String propertyName = evt.getPropertyName();
        if (propertyName.equals(
                AttributeInstanceController.ELEMENT_ATTR_VALUE)) {
            Integer newValue = (Integer) evt.getNewValue();
            spinner.setValue(newValue);
        } else if (propertyName.equals(
                AtomDefinitionController.ATOM_MAXVALUE)) {
            Integer newValue = (Integer) evt.getNewValue();
            spinner.setMax(newValue);
        } else if (propertyName.equals(
                AtomDefinitionController.ATOM_MINVALUE)) {
            Integer newValue = (Integer) evt.getNewValue();
            spinner.setMin(newValue);
        }
    }

}
