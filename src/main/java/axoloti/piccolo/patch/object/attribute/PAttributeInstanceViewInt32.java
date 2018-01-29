package axoloti.piccolo.patch.object.attribute;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.attribute.AttributeInstanceController;
import axoloti.patch.object.attribute.AttributeInstanceInt32;
import axoloti.piccolo.components.PLabelComponent;
import axoloti.piccolo.components.control.PCtrlEvent;
import axoloti.piccolo.components.control.PCtrlListener;
import axoloti.piccolo.components.control.PVSliderComponent;
import java.awt.Dimension;

public class PAttributeInstanceViewInt32 extends PAttributeInstanceViewInt {

    PVSliderComponent slider;
    PLabelComponent vlabel;

    public PAttributeInstanceViewInt32(AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public AttributeInstanceInt32 getModel() {
        return (AttributeInstanceInt32) super.getModel();
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        slider = new PVSliderComponent(
            getModel().getValue(),
            getModel().getModel().getMinValue(),
            getModel().getModel().getMaxValue(), 1, axoObjectInstanceView);

        if (getModel().getValue() < getModel().getModel().getMinValue()) {
            getModel().setValue(getModel().getModel().getMinValue());
        }
        if (getModel().getValue() > getModel().getModel().getMaxValue()) {
            getModel().setValue(getModel().getModel().getMaxValue());
        }

        Dimension d = slider.getSize();
        d.width = 128;
        d.height = 22;
        slider.setMaximumSize(d);
        slider.setMinimumSize(d);
        slider.setPreferredSize(d);
        slider.setSize(d);

        addChild(slider);
        vlabel = new PLabelComponent("       " + getModel().getValue());
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
                getModel().setValue((int) slider.getValue());
                vlabel.setText("" + getModel().getValue());
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
