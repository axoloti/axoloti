package axoloti.attributeviews;

import axoloti.attribute.AttributeInstanceInt32;
import axoloti.objectviews.AxoObjectInstanceView;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class AttributeInstanceViewInt32 extends AttributeInstanceViewInt {

    AttributeInstanceInt32 attributeInstance;
    JSlider slider;
    JLabel vlabel;

    public AttributeInstanceViewInt32(AttributeInstanceInt32 attributeInstance, AxoObjectInstanceView axoObjectInstanceView) {
        super(attributeInstance, axoObjectInstanceView);
        this.attributeInstance = attributeInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        slider = new JSlider();
        Dimension d = slider.getSize();
        d.width = 128;
        d.height = 22;
        if (attributeInstance.getValue() < attributeInstance.getDefinition().getMinValue()) {
            attributeInstance.setValue(attributeInstance.getDefinition().getMinValue());
        }
        if (attributeInstance.getValue() > attributeInstance.getDefinition().getMaxValue()) {
            attributeInstance.setValue(attributeInstance.getDefinition().getMaxValue());
        }
        slider.setMinimum(attributeInstance.getDefinition().getMinValue());
        slider.setMaximum(attributeInstance.getDefinition().getMaxValue());
        slider.setValue(attributeInstance.getValue());
        slider.setMaximumSize(d);
        slider.setMinimumSize(d);
        slider.setPreferredSize(d);
        slider.setSize(d);
        add(slider);
        vlabel = new JLabel();
        vlabel.setText("       " + attributeInstance.getValue());
        add(vlabel);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                attributeInstance.setValue(slider.getValue());
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
