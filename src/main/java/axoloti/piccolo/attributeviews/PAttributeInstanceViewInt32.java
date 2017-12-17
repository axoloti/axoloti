package axoloti.piccolo.attributeviews;

import axoloti.patch.object.attribute.AttributeInstanceInt32;
import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.piccolo.components.PLabelComponent;
import axoloti.piccolo.components.control.PCtrlEvent;
import axoloti.piccolo.components.control.PCtrlListener;
import axoloti.piccolo.components.control.PVSliderComponent;
import java.awt.Dimension;

public class PAttributeInstanceViewInt32 extends PAttributeInstanceViewInt {

    AttributeInstanceInt32 attributeInstance;
    PVSliderComponent slider;
    PLabelComponent vlabel;

    public PAttributeInstanceViewInt32(AttributeInstanceInt32 attributeInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attributeInstance, axoObjectInstanceView);
        this.attributeInstance = attributeInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        slider = new PVSliderComponent(attributeInstance.getValue(),
                attributeInstance.getModel().getMinValue(),
                attributeInstance.getModel().getMaxValue(), 1, axoObjectInstanceView);

        if (attributeInstance.getValue() < attributeInstance.getModel().getMinValue()) {
            attributeInstance.setValue(attributeInstance.getModel().getMinValue());
        }
        if (attributeInstance.getValue() > attributeInstance.getModel().getMaxValue()) {
            attributeInstance.setValue(attributeInstance.getModel().getMaxValue());
        }

        Dimension d = slider.getSize();
        d.width = 128;
        d.height = 22;
        slider.setMaximumSize(d);
        slider.setMinimumSize(d);
        slider.setPreferredSize(d);
        slider.setSize(d);

        addChild(slider);
        vlabel = new PLabelComponent("       " + attributeInstance.getValue());
        addChild(vlabel);
        slider.addPCtrlListener(new PCtrlListener() {
            @Override
            public void PCtrlAdjustmentBegin(PCtrlEvent e) {
            }

            @Override
            public void PCtrlAdjusted(PCtrlEvent e) {
            }

            @Override
            public void PCtrlAdjustmentFinished(PCtrlEvent e) {
                attributeInstance.setValue((int) slider.getValue());
                vlabel.setText("" + attributeInstance.getValue());
            }
        });
    }

    @Override
    public void Lock() {
        if (slider != null) {
            slider.setEnabled(false);
        }
    }

    @Override
    public void UnLock() {
        if (slider != null) {
            slider.setEnabled(true);
        }
    }
}
