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
package components;

import axoloti.Modulation;
import axoloti.Modulator;
import axoloti.datatypes.ValueFrac32;
import axoloti.parameters.ParameterInstance;
import axoloti.parameters.ParameterInstanceFrac32;
import components.control.ACtrlComponent;
import components.control.HSliderComponent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 *
 * @author Johannes Taelman
 */
public class AssignModulatorMenuItems {

    double valueBeforeAdjustment;

    public AssignModulatorMenuItems(final ParameterInstance parameterInstance, JComponent parent) {
        final ArrayList<HSliderComponent> hsls = new ArrayList<HSliderComponent>();

        //this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        hsls.clear();

        for (Modulator m : parameterInstance.getObjectInstance().getPatchModel().getPatchModulators()) {
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
            String modlabel;
            if ((m.name == null) || (m.name.isEmpty())) {
                modlabel = m.objinst.getInstanceName();
            } else {
                modlabel = m.objinst.getInstanceName() + ":" + m.name;
            }
            p.add(new JLabel(modlabel + " "));
            HSliderComponent hsl = new HSliderComponent();
            if (parameterInstance.getModulators() != null) {
                List<Modulation> modulators = parameterInstance.getModulators();
                for (Modulation n : modulators) {
                    if (m.Modulations.contains(n)) {
                        System.out.println("modulation restored " + n.getValue());
                        hsl.setValue(n.getValue());
                    }
                }
            }
            hsl.addPropertyChangeListener(new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals(ACtrlComponent.PROP_VALUE_ADJ_BEGIN)) {
                        valueBeforeAdjustment = ((HSliderComponent) evt.getSource()).getValue();
                    } else if (evt.getPropertyName().equals(ACtrlComponent.PROP_VALUE_ADJ_END)) {
                    } else if (evt.getPropertyName().equals(ACtrlComponent.PROP_VALUE)) {
                    int i = hsls.indexOf(evt.getSource());
                        //                            System.out.println("ctrl " + i + parameterInstance.axoObj.patch.Modulators.get(i).objinst.InstanceName);
                        ValueFrac32 v = new ValueFrac32(((HSliderComponent) evt.getSource()).getValue());
                        ((ParameterInstanceFrac32) parameterInstance).updateModulation(i, v.getDouble());
                    }
                }
            });
            hsls.add(hsl);
            p.add(hsl);
            parent.add(p);
        }
        if (parameterInstance.getObjectInstance().getPatchModel().getPatchModulators().isEmpty()) {
            JMenuItem d = new JMenuItem("no modulation sources in patch");
            d.setEnabled(false);
            parent.add(d);
        }
    }
}
