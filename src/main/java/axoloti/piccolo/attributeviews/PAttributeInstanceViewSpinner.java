package axoloti.piccolo.attributeviews;

import axoloti.patch.object.attribute.AttributeInstanceSpinner;
import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.piccolo.components.control.PCtrlEvent;
import axoloti.piccolo.components.control.PCtrlListener;
import axoloti.piccolo.components.control.PNumberBoxComponent;

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

        if (value < attributeInstance.getModel().getMinValue()) {
            attributeInstance.setValue(attributeInstance.getModel().getMinValue());
        }
        if (value > attributeInstance.getModel().getMaxValue()) {
            attributeInstance.setValue(attributeInstance.getModel().getMaxValue());
        }
        spinner = new PNumberBoxComponent(value, attributeInstance.getModel().getMinValue(),
                attributeInstance.getModel().getMaxValue(), 1.0, axoObjectInstanceView);
        addChild(spinner);
        spinner.addPCtrlListener(new PCtrlListener() {
            @Override
            public void PCtrlAdjusted(PCtrlEvent e) {
                attributeInstance.setValue((int) spinner.getValue());
            }

            @Override
            public void PCtrlAdjustmentBegin(PCtrlEvent e) {
                //attributeInstance.setValueBeforeAdjustment(attributeInstance.getValue());
            }

            @Override
            public void PCtrlAdjustmentFinished(PCtrlEvent e) {
                //if (attributeInstance.getValue() != attributeInstance.getValueBeforeAdjustment()) {
                //    attributeInstance.getObjectInstance().getPatchModel().setDirty();
                //}
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
