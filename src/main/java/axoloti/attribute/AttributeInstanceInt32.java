/**
 * Copyright (C) 2013, 2014 Johannes Taelman
 *
 * This file is part of Axoloti.
 *
 * Axoloti is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Axoloti is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Axoloti. If not, see <http://www.gnu.org/licenses/>.
 */
package axoloti.attribute;

import axoloti.attributedefinition.AxoAttributeInt32;
import axoloti.attributedefinition.AxoAttribute;
import axoloti.object.AxoObjectInstance;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Johannes Taelman
 */
public class AttributeInstanceInt32 extends AttributeInstanceInt {

    JSlider slider;
    JLabel vlabel;

    public AttributeInstanceInt32() {
    }

    public AttributeInstanceInt32(AxoAttribute param, AxoObjectInstance axoObj1) {
        super(param, axoObj1);
//        PostConstructor();
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        slider = new JSlider();
        Dimension d = slider.getSize();
        d.width = 128;
        d.height = 22;
        if (value < ((AxoAttributeInt32) attr).getMinValue()) {
            value = ((AxoAttributeInt32) attr).getMinValue();
        }
        if (value > ((AxoAttributeInt32) attr).getMaxValue()) {
            value = ((AxoAttributeInt32) attr).getMaxValue();
        }
        slider.setMinimum(((AxoAttributeInt32) attr).getMinValue());
        slider.setMaximum(((AxoAttributeInt32) attr).getMaxValue());
        slider.setValue(value);
        slider.setMaximumSize(d);
        slider.setMinimumSize(d);
        slider.setPreferredSize(d);
        slider.setSize(d);
        add(slider);
        vlabel = new JLabel();
        vlabel.setText("       " + value);
        add(vlabel);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                value = slider.getValue();
                vlabel.setText("" + value);
            }
        });
    }

    @Override
    public String CValue() {
        return "" + value;
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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
