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

import axoloti.PatchModel;
import axoloti.Preset;
import axoloti.parameterviews.ParameterInstanceView;
import components.control.ACtrlComponent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 *
 * @author Johannes Taelman
 */
public class AssignPresetPanel extends JPanel {

    final ParameterInstanceView parameterInstanceView;
    final ArrayList<ACtrlComponent> ctrls;

    public AssignPresetPanel(ParameterInstanceView parameterInstanceView) {
        this.parameterInstanceView = parameterInstanceView;
        int n = parameterInstanceView.getModel().getObjectInstance().getPatchModel().getNPresets();
        ctrls = new ArrayList<ACtrlComponent>(n);

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        for (int i = 0; i < n; i++) {
            c.gridx = 0;
            c.gridy = i;
            JCheckBox cb = new JCheckBox("Preset " + (i + 1));
            cb.addActionListener(cbActionListener);
            add(cb, c);
            c.gridx = 1;
            ACtrlComponent ctrl = parameterInstanceView.CreateControl();
            ctrls.add(ctrl);
            Preset p = parameterInstanceView.getModel().getPreset(i + 1);
            if (p != null) {
                cb.setSelected(true);
                Object o = p.getValue();
                if (o instanceof Integer) {
                    ctrl.setValue((Integer)p.getValue());
                } else {
                    ctrl.setValue((Double)p.getValue());                        
                }
            } else {
                ctrl.setEnabled(false);
                //ctrl.setValue(parameterInstanceView.getModel().getValue());
            }
            ctrl.addPropertyChangeListener(ctrlListener);
            add(ctrl, c);
        }
    }

    ActionListener cbActionListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            String[] s = e.getActionCommand().split(" ");
            int i = Integer.parseInt(s[1]) - 1;
            if (((JCheckBox) e.getSource()).isSelected()) {
                parameterInstanceView.getController().AddPreset(i + 1, parameterInstanceView.getModel().getValue());
                ctrls.get(i).setEnabled(true);
                parameterInstanceView.getController().addMetaUndo("add preset to parameter " + parameterInstanceView.getModel().getName());
                Object v = parameterInstanceView.getModel().getPreset(i + 1).getValue();
                double vd = 0.0;
                if (v instanceof Integer) {
                    vd = (Integer)v;
                } else if (v instanceof Double) {
                    vd = (Double)v;
                }
                ctrls.get(i).setValue(vd);
            } else {
                ctrls.get(i).setEnabled(false);
                parameterInstanceView.getController().addMetaUndo("remove preset from parameter " + parameterInstanceView.getModel().getName());
                parameterInstanceView.getController().RemovePreset(i + 1);
            }
            PatchModel patchModel = parameterInstanceView.getModel().getObjectInstance().getPatchModel();
            patchModel.presetUpdatePending = true;
        }

    };

    double valueBeforeAdjustment;
    
    PropertyChangeListener ctrlListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(ACtrlComponent.PROP_VALUE_ADJ_BEGIN)) {
                parameterInstanceView.getController().addMetaUndo("change preset of parameter " + parameterInstanceView.getModel().getName());
                int i = ctrls.indexOf(evt.getSource());
                if (i >= 0) {
                    valueBeforeAdjustment = ctrls.get(i).getValue();
                }
            } else if (evt.getPropertyName().equals(ACtrlComponent.PROP_VALUE_ADJ_END)) {
                int i = ctrls.indexOf(evt.getSource());
                if (i >= 0) {
                    if (valueBeforeAdjustment != ctrls.get(i).getValue()) {
                        PatchModel patchModel = parameterInstanceView.getModel().getObjectInstance().getPatchModel();
                    }
                }
            } else if (evt.getPropertyName().equals(ACtrlComponent.PROP_VALUE)) {
                int i = ctrls.indexOf(evt.getSource());
                if (i >= 0) {
                    if (ctrls.get(i).isEnabled()) {
                        // FIXME
                        if (parameterInstanceView.getModel().getValue() instanceof Integer) {
                            parameterInstanceView.getController().AddPreset(i + 1, (int) ctrls.get(i).getValue());
                        } else if (parameterInstanceView.getModel().getValue() instanceof Double) {
                            parameterInstanceView.getController().AddPreset(i + 1, (double)ctrls.get(i).getValue());
                        }
                    }
                }
                parameterInstanceView.getModel().getObjectInstance().getPatchModel().presetUpdatePending = true;
            }
        }
    };
}
