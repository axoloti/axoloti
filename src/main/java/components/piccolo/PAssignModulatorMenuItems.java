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
package components.piccolo;

import axoloti.Modulation;
import axoloti.Modulator;
import axoloti.datatypes.ValueFrac32;
import axoloti.piccolo.parameterviews.PParameterInstanceViewFrac32UMap;
import components.control.HSliderComponent;
import java.beans.PropertyChangeEvent;
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
public class PAssignModulatorMenuItems {

    double valueBeforeAdjustment;

    public PAssignModulatorMenuItems(final PParameterInstanceViewFrac32UMap parameterInstanceView, JComponent parent) {
        final ArrayList<HSliderComponent> hsls = new ArrayList<>();

        hsls.clear();

        for (Modulator m : parameterInstanceView.getModel().getObjectInstance().getPatchModel().getPatchModulators()) {
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
            if (parameterInstanceView.getModel().getModulators() != null) {
                List<Modulation> modulators = parameterInstanceView.getModel().getModulators();
                for (Modulation n : modulators) {
                    if (m.Modulations.contains(n)) {
                        System.out.println("modulation restored " + n.getValue());
                        hsl.setValue(n.getValue());
                    }
                }
            }/*
            hsl.addACtrlListener(new ACtrlListener() {
                @Override
                public void ACtrlAdjusted(ACtrlEvent e) {
                    int i = hsls.indexOf(e.getSource());
                    ValueFrac32 v = new ValueFrac32(((HSliderComponent) e.getSource()).getValue());
                    parameterInstanceView.updateModulation(i, v.getDouble());
                }

                @Override
                public void ACtrlAdjustmentBegin(ACtrlEvent e) {
                    valueBeforeAdjustment = ((HSliderComponent) e.getSource()).getValue();
                }

                @Override
                public void ACtrlAdjustmentFinished(ACtrlEvent e) {
                    double vnew = ((HSliderComponent) e.getSource()).getValue();
                    if (vnew != valueBeforeAdjustment) {
                        //parameterInstanceView.getModel().SetDirty();
                    }
                }

                @Override
                public void PropertyChanged(PropertyChangeEvent evt) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            });*/
            hsls.add(hsl);
            p.add(hsl);
            parent.add(p);
        }
        if (parameterInstanceView.getModel().getObjectInstance().getPatchModel().getPatchModulators().isEmpty()) {
            JMenuItem d = new JMenuItem("no modulation sources in patch");
            d.setEnabled(false);
            parent.add(d);
        }
    }
}
