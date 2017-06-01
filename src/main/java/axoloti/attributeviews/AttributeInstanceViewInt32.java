package axoloti.attributeviews;

import axoloti.attribute.AttributeInstanceController;
import axoloti.attribute.AttributeInstanceInt32;
import axoloti.objectviews.IAxoObjectInstanceView;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class AttributeInstanceViewInt32 extends AttributeInstanceViewInt {

    JSlider slider;
    JLabel vlabel;

    public AttributeInstanceViewInt32(AttributeInstanceInt32 attributeInstance, AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attributeInstance, controller, axoObjectInstanceView);
    }

    @Override
    public AttributeInstanceInt32 getAttributeInstance() {
        return (AttributeInstanceInt32) super.getAttributeInstance();
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        slider = new JSlider();
        Dimension d = slider.getSize();
        d.width = 128;
        d.height = 22;
        if (getAttributeInstance().getValue() < getAttributeInstance().getDefinition().getMinValue()) {
            getAttributeInstance().setValue(getAttributeInstance().getDefinition().getMinValue());
        }
        if (getAttributeInstance().getValue() > getAttributeInstance().getDefinition().getMaxValue()) {
            getAttributeInstance().setValue(getAttributeInstance().getDefinition().getMaxValue());
        }
        slider.setMinimum(getAttributeInstance().getDefinition().getMinValue());
        slider.setMaximum(getAttributeInstance().getDefinition().getMaxValue());
        slider.setValue(getAttributeInstance().getValue());
        slider.setMaximumSize(d);
        slider.setMinimumSize(d);
        slider.setPreferredSize(d);
        slider.setSize(d);
        add(slider);
        vlabel = new JLabel();
        vlabel.setText("       " + getAttributeInstance().getValue());
        add(vlabel);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                getAttributeInstance().setValue(slider.getValue());
                vlabel.setText("" + getAttributeInstance().getValue());
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
