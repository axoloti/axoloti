package axoloti.attributeviews;

import axoloti.attribute.AttributeInstanceSpinner;
import axoloti.objectviews.AxoObjectInstanceView;
import components.control.ACtrlEvent;
import components.control.ACtrlListener;
import components.control.NumberBoxComponent;

public class AttributeInstanceViewSpinner extends AttributeInstanceViewInt {

    AttributeInstanceSpinner attributeInstance;
    NumberBoxComponent spinner;

    public AttributeInstanceViewSpinner(AttributeInstanceSpinner attributeInstance, AxoObjectInstanceView axoObjectInstanceView) {
        super(attributeInstance, axoObjectInstanceView);
        this.attributeInstance = attributeInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        int value = attributeInstance.getValue();

        if (value < attributeInstance.getDefinition().getMinValue()) {
            attributeInstance.setValue(attributeInstance.getDefinition().getMinValue());
        }
        if (value > attributeInstance.getDefinition().getMaxValue()) {
            attributeInstance.setValue(attributeInstance.getDefinition().getMaxValue());
        }
        spinner = new NumberBoxComponent(value, attributeInstance.getDefinition().getMinValue(), attributeInstance.getDefinition().getMaxValue(), 1.0);
        spinner.setParentAxoObjectInstanceView(axoObjectInstanceView);
        add(spinner);
        spinner.addACtrlListener(new ACtrlListener() {
            @Override
            public void ACtrlAdjusted(ACtrlEvent e) {
                attributeInstance.setValue((int) spinner.getValue());
            }

            @Override
            public void ACtrlAdjustmentBegin(ACtrlEvent e) {
                attributeInstance.setValueBeforeAdjustment(attributeInstance.getValue());
            }

            @Override
            public void ACtrlAdjustmentFinished(ACtrlEvent e) {
                if (attributeInstance.getValue() != attributeInstance.getValueBeforeAdjustment()) {
                    attributeInstance.getObjectInstance().getPatchModel().SetDirty();
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
}
