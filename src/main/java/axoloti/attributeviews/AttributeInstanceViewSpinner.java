package axoloti.attributeviews;

import axoloti.attribute.AttributeInstanceController;
import axoloti.attribute.AttributeInstanceSpinner;
import axoloti.objectviews.IAxoObjectInstanceView;
import components.control.ACtrlEvent;
import components.control.ACtrlListener;
import components.control.NumberBoxComponent;
import java.beans.PropertyChangeEvent;

class AttributeInstanceViewSpinner extends AttributeInstanceViewInt {

    NumberBoxComponent spinner;

    public AttributeInstanceViewSpinner(AttributeInstanceSpinner attributeInstance, AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attributeInstance, controller, axoObjectInstanceView);
    }

    @Override
    public AttributeInstanceSpinner getAttributeInstance() {
        return (AttributeInstanceSpinner) super.getAttributeInstance();
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        Integer ival = getAttributeInstance().getValue();
        int value = ival;

        if (value < getAttributeInstance().getModel().getMinValue()) {
            getAttributeInstance().setValue(getAttributeInstance().getModel().getMinValue());
        }
        if (value > getAttributeInstance().getModel().getMaxValue()) {
            getAttributeInstance().setValue(getAttributeInstance().getModel().getMaxValue());
        }
        spinner = new NumberBoxComponent(value, getAttributeInstance().getModel().getMinValue(), getAttributeInstance().getModel().getMaxValue(), 1.0);
        add(spinner);
        spinner.addACtrlListener(new ACtrlListener() {
            @Override
            public void ACtrlAdjusted(ACtrlEvent e) {
//                attributeInstance.setValue((int) spinner.getValue());
                controller.changeValue((Integer) (int) spinner.getValue());
            }

            @Override
            public void ACtrlAdjustmentBegin(ACtrlEvent e) {
                //attributeInstance.setValueBeforeAdjustment(attributeInstance.getValue());
            }

            @Override
            public void ACtrlAdjustmentFinished(ACtrlEvent e) {
                if (getAttributeInstance().getValue() != getAttributeInstance().getValueBeforeAdjustment()) {
                    //attributeInstance.getObjectInstance().getPatchModel().setDirty();
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
        if (evt.getPropertyName().equals(
                AttributeInstanceController.ELEMENT_ATTR_VALUE)) {
            Integer newValue = (Integer) evt.getNewValue();
            spinner.setValue(newValue);
        }
    }

}
