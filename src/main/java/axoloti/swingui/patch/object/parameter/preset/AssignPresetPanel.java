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
package axoloti.swingui.patch.object.parameter.preset;

import axoloti.mvc.FocusEdit;
import axoloti.patch.PatchModel;
import axoloti.patch.object.parameter.preset.Preset;
import axoloti.swingui.components.control.ACtrlComponent;
import axoloti.swingui.patch.object.parameter.ParameterInstanceView;
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
        int n = parameterInstanceView.getDModel().getObjectInstance().getParent().getNPresets();
        ctrls = new ArrayList<>(n);
        initComponent(n);
    }

    private void initComponent(int n) {

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        for (int i = 0; i < n; i++) {
            c.gridx = 0;
            c.gridy = i;
            JCheckBox cb = new JCheckBox("Preset " + (i + 1));
            cb.addActionListener(cbActionListener);
            add(cb, c);
            c.gridx = 1;
            ACtrlComponent ctrl = parameterInstanceView.createControl();
            ctrls.add(ctrl);
            Preset p = parameterInstanceView.getDModel().getPreset(i + 1);
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

    private FocusEdit getFocusEdit() {
        return parameterInstanceView.getFocusEdit();
    }

    ActionListener cbActionListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            String[] s = e.getActionCommand().split(" ");
            int i = Integer.parseInt(s[1]) - 1;
            if (((JCheckBox) e.getSource()).isSelected()) {
                parameterInstanceView.getDModel().getController().addMetaUndo(
                        "add preset to parameter "
                        + parameterInstanceView.getDModel().getName(),
                        getFocusEdit());
                parameterInstanceView.getDModel().getController().addPreset(i + 1, parameterInstanceView.getDModel().getValue());
                ctrls.get(i).setEnabled(true);
                Object v = parameterInstanceView.getDModel().getPreset(i + 1).getValue();
                double vd = 0.0;
                if (v instanceof Integer) {
                    vd = (Integer)v;
                } else if (v instanceof Double) {
                    vd = (Double)v;
                }
                ctrls.get(i).setValue(vd);
            } else {
                ctrls.get(i).setEnabled(false);
                parameterInstanceView.getDModel().getController().addMetaUndo(
                        "remove preset from parameter "
                        + parameterInstanceView.getDModel().getName(),
                        getFocusEdit());
                parameterInstanceView.getDModel().getController().removePreset(i + 1);
            }
        }

    };

    double valueBeforeAdjustment;

    PropertyChangeListener ctrlListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(ACtrlComponent.PROP_VALUE_ADJ_BEGIN)) {
                parameterInstanceView.getDModel().getController().addMetaUndo(
                        "change preset of parameter "
                        + parameterInstanceView.getDModel().getName(),
                        getFocusEdit());
                int i = ctrls.indexOf(evt.getSource());
                if (i >= 0) {
                    valueBeforeAdjustment = ctrls.get(i).getValue();
                }
            } else if (evt.getPropertyName().equals(ACtrlComponent.PROP_VALUE_ADJ_END)) {
                int i = ctrls.indexOf(evt.getSource());
                if (i >= 0) {
                    if (valueBeforeAdjustment != ctrls.get(i).getValue()) {
                        PatchModel patchModel = parameterInstanceView.getDModel().getObjectInstance().getParent();
                    }
                }
            } else if (evt.getPropertyName().equals(ACtrlComponent.PROP_VALUE)) {
                int i = ctrls.indexOf(evt.getSource());
                if (i >= 0) {
                    if (ctrls.get(i).isEnabled()) {
                        if (parameterInstanceView.getDModel().getValue() instanceof Integer) {
                            parameterInstanceView.getDModel().getController().addPreset(i + 1, (int) ctrls.get(i).getValue());
                        } else if (parameterInstanceView.getDModel().getValue() instanceof Double) {
                            parameterInstanceView.getDModel().getController().addPreset(i + 1, (double) ctrls.get(i).getValue());
                        }
                    }
                }
            }
        }
    };
}
