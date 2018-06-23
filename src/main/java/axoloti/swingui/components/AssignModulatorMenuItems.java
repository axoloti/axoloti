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
package axoloti.swingui.components;

import axoloti.patch.Modulation;
import axoloti.patch.Modulator;
import axoloti.datatypes.ValueFrac32;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstanceFrac32;
import static axoloti.swingui.components.control.ACtrlComponent.PROP_VALUE;
import static axoloti.swingui.components.control.ACtrlComponent.PROP_VALUE_ADJ_BEGIN;
import static axoloti.swingui.components.control.ACtrlComponent.PROP_VALUE_ADJ_END;
import axoloti.swingui.components.control.HSliderComponent;
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

    public AssignModulatorMenuItems(final ParameterInstance parameterInstance, JComponent parent) {
        final ArrayList<HSliderComponent> hsls = new ArrayList<>();

        //this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        hsls.clear();

        for (Modulator m : parameterInstance.getObjectInstance().getParent().getModulators()) {
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
            String modlabel;
            if ((m.getName() == null) || (m.getName().isEmpty())) {
                modlabel = m.getObjectInstance().getInstanceName();
            } else {
                modlabel = m.getObjectInstance().getInstanceName() + ":" + m.getName();
            }
            p.add(new JLabel(modlabel + " "));
            HSliderComponent hsl = new HSliderComponent();
            if (parameterInstance.getModulations() != null) {
                List<Modulation> modulators = parameterInstance.getModulations();
                for (Modulation n : modulators) {
                    if (n.getModulator() == m) {
                        System.out.println("modulation restored " + n.getValue());
                        hsl.setValue(n.getValue());
                    }
                }
            }
            hsl.addPropertyChangeListener(new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals(PROP_VALUE_ADJ_BEGIN)) {
                        parameterInstance.getController().addMetaUndo("change modulation of parameter " + parameterInstance.getName());
                    } else if (evt.getPropertyName().equals(PROP_VALUE_ADJ_END)) {
                    } else if (evt.getPropertyName().equals(PROP_VALUE)) {
                        int i = hsls.indexOf(evt.getSource());
                        // System.out.println("ctrl " + i + parameterInstance.axoObj.patch.Modulators.get(i).objinst.InstanceName);
                        ValueFrac32 v = new ValueFrac32(((HSliderComponent) evt.getSource()).getValue());
                        ((ParameterInstanceFrac32) parameterInstance).getController().changeModulation(m, v.getDouble());
                    }
                }
            });
            hsls.add(hsl);
            p.add(hsl);
            parent.add(p);
        }
        if (parameterInstance.getObjectInstance().getParent().getModulators().isEmpty()) {
            JMenuItem d = new JMenuItem("no modulation sources in patch");
            d.setEnabled(false);
            parent.add(d);
        }
    }
}
