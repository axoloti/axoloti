package axoloti.attributeviews;

import axoloti.attribute.AttributeInstanceController;
import axoloti.attribute.AttributeInstanceInt32;
import axoloti.objectviews.IAxoObjectInstanceView;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@Deprecated // we shall not use Swing controls in a patch
class AttributeInstanceViewInt32 extends AttributeInstanceViewInt {

    JSlider slider;
    JLabel vlabel;

    AttributeInstanceViewInt32(AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public AttributeInstanceInt32 getModel() {
        return (AttributeInstanceInt32) super.getModel();
    }

    @Override
    void PostConstructor() {
        super.PostConstructor();
        slider = new JSlider();
        Dimension d = slider.getSize();
        d.width = 128;
        d.height = 22;
        if (getModel().getValue() < getModel().getModel().getMinValue()) {
            getModel().setValue(getModel().getModel().getMinValue());
        }
        if (getModel().getValue() > getModel().getModel().getMaxValue()) {
            getModel().setValue(getModel().getModel().getMaxValue());
        }
        slider.setMinimum(getModel().getModel().getMinValue());
        slider.setMaximum(getModel().getModel().getMaxValue());
        slider.setValue(getModel().getValue());
        slider.setMaximumSize(d);
        slider.setMinimumSize(d);
        slider.setPreferredSize(d);
        slider.setSize(d);
        add(slider);
        vlabel = new JLabel();
        vlabel.setText("       " + getModel().getValue());
        add(vlabel);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                getModel().setValue(slider.getValue());
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
