package axoloti.swingui.patch.object.attribute;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.attribute.AttributeInstanceInt32;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@Deprecated // we shall not use Swing controls in a patch
class AttributeInstanceViewInt32 extends AttributeInstanceViewInt {

    private JSlider slider;
    private JLabel vlabel;

    AttributeInstanceViewInt32(AttributeInstance attribute, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attribute, axoObjectInstanceView);
        initComponents();
    }

    @Override
    public AttributeInstanceInt32 getDModel() {
        return (AttributeInstanceInt32) super.getDModel();
    }

    private void initComponents() {
        slider = new JSlider();
        Dimension d = slider.getSize();
        d.width = 128;
        d.height = 22;
        if (getDModel().getValueInteger() < getDModel().getDModel().getMinValue()) {
            getDModel().setValue(getDModel().getDModel().getMinValue());
        }
        if (getDModel().getValueInteger() > getDModel().getDModel().getMaxValue()) {
            getDModel().setValue(getDModel().getDModel().getMaxValue());
        }
        slider.setMinimum(getDModel().getDModel().getMinValue());
        slider.setMaximum(getDModel().getDModel().getMaxValue());
        slider.setValue(getDModel().getValueInteger());
        slider.setMaximumSize(d);
        slider.setMinimumSize(d);
        slider.setPreferredSize(d);
        slider.setSize(d);
        add(slider);
        vlabel = new JLabel();
        vlabel.setText("       " + getDModel().getValue());
        add(vlabel);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                getDModel().setValue(slider.getValue());
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
