package axoloti.piccolo.patch.object.attribute;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.attribute.AttributeInstanceInt32;
import axoloti.piccolo.components.PLabelComponent;
import axoloti.piccolo.components.control.PCtrlEvent;
import axoloti.piccolo.components.control.PCtrlListener;
import axoloti.piccolo.components.control.PVSliderComponent;
import java.awt.Dimension;

class PAttributeInstanceViewInt32 extends PAttributeInstanceViewInt {

    PVSliderComponent slider;
    PLabelComponent vlabel;

    PAttributeInstanceViewInt32(AttributeInstance attribute, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attribute, axoObjectInstanceView);
        initComponents();
    }

    @Override
    public AttributeInstanceInt32 getDModel() {
        return (AttributeInstanceInt32) super.getDModel();
    }

    private void initComponents() {
        slider = new PVSliderComponent(
                getDModel().getValueInteger(),
                getDModel().getDModel().getMinValue(),
            getDModel().getDModel().getMaxValue(), 1, axoObjectInstanceView);

        if (getDModel().getValueInteger() < getDModel().getDModel().getMinValue()) {
            getDModel().setValue(getDModel().getDModel().getMinValue());
        }
        if (getDModel().getValueInteger() > getDModel().getDModel().getMaxValue()) {
            getDModel().setValue(getDModel().getDModel().getMaxValue());
        }

        Dimension d = slider.getSize();
        d.width = 128;
        d.height = 22;
        slider.setMaximumSize(d);
        slider.setMinimumSize(d);
        slider.setPreferredSize(d);
        slider.setSize(d);

        addChild(slider);
        vlabel = new PLabelComponent("       " + getDModel().getValue());
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
                getDModel().setValue((int) slider.getValue());
                vlabel.setText("" + getDModel().getValue());
            }
        });
    }

    @Override
    public void lock() {
        if (slider != null) {
            slider.setEnabled(false);
        }
    }

    @Override
    public void unlock() {
        if (slider != null) {
            slider.setEnabled(true);
        }
    }
}
