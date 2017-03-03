package axoloti.piccolo.attributeviews;

import axoloti.attribute.AttributeInstanceSpinner;
import axoloti.objectviews.IAxoObjectInstanceView;
import components.piccolo.control.PCtrlEvent;
import components.piccolo.control.PCtrlListener;
import components.piccolo.control.PNumberBoxComponent;

public class PAttributeInstanceViewSpinner extends PAttributeInstanceViewInt {

    AttributeInstanceSpinner attributeInstance;
    PNumberBoxComponent spinner;

    public PAttributeInstanceViewSpinner(AttributeInstanceSpinner attributeInstance, IAxoObjectInstanceView axoObjectInstanceView) {
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
        spinner = new PNumberBoxComponent(value, attributeInstance.getDefinition().getMinValue(),
                attributeInstance.getDefinition().getMaxValue(), 1.0, axoObjectInstanceView);
        addChild(spinner);
        spinner.addPCtrlListener(new PCtrlListener() {
            @Override
            public void PCtrlAdjusted(PCtrlEvent e) {
                attributeInstance.setValue((int) spinner.getValue());
            }

            @Override
            public void PCtrlAdjustmentBegin(PCtrlEvent e) {
                attributeInstance.setValueBeforeAdjustment(attributeInstance.getValue());
            }

            @Override
            public void PCtrlAdjustmentFinished(PCtrlEvent e) {
                if (attributeInstance.getValue() != attributeInstance.getValueBeforeAdjustment()) {
                    attributeInstance.getObjectInstance().getPatchModel().setDirty();
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
