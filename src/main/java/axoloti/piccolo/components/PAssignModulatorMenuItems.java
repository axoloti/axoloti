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
package axoloti.piccolo.components;

import axoloti.patch.Modulation;
import axoloti.patch.Modulator;
import axoloti.piccolo.patch.object.parameter.PParameterInstanceViewFrac32UMap;
import axoloti.swingui.components.control.HSliderComponent;
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

        for (Modulator m : parameterInstanceView.getDModel().getObjectInstance().getParent().getModulators()) {
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
            if (parameterInstanceView.getDModel().getModulations() != null) {
                List<Modulation> modulators = parameterInstanceView.getDModel().getModulations();
                for (Modulation n : modulators) {
                    if (m.getModulations().contains(n)) {
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
        if (parameterInstanceView.getDModel().getObjectInstance().getParent().getModulators().isEmpty()) {
            JMenuItem d = new JMenuItem("no modulation sources in patch");
            d.setEnabled(false);
            parent.add(d);
        }
    }
}
