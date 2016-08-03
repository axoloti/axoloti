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

import axoloti.PatchGUI;
import axoloti.Preset;
import axoloti.ZoomUI;
import axoloti.datatypes.ValueFrac32;
import axoloti.datatypes.ValueInt32;
import axoloti.parameters.ParameterInstance;
import axoloti.utils.Constants;
import components.control.ACtrlComponent;
import components.control.ACtrlEvent;
import components.control.ACtrlListener;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JPanel;

/**
 *
 * @author Johannes Taelman
 */
public class AssignPresetPanel extends JPanel {

    final ParameterInstance param;
    final ArrayList<ACtrlComponent> ctrls;
    final ArrayList<JLayer> ctrlLayers;

    public AssignPresetPanel(ParameterInstance param) {
        this.param = param;
        int n = param.GetObjectInstance().getPatch().getSettings().GetNPresets();
        ctrls = new ArrayList<ACtrlComponent>(n);
        ctrlLayers = new ArrayList<JLayer>(n);

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        for (int i = 0; i < n; i++) {
            c.gridx = 0;
            c.gridy = i;
            JCheckBox cb = new JCheckBox("Preset " + (i + 1));
            cb.addActionListener(cbActionListener);
            add(cb, c);
            c.gridx = 1;
            
            PatchGUI patch = (PatchGUI) param.GetObjectInstance().patch;
            double scale = patch.zoomUI.getScale();
            
            ZoomUI zoomUI = new ZoomUI(scale,
            Constants.ZOOM_STEP,
            Constants.MAXIMUM_ZOOM,
            Constants.MINIMUM_ZOOM,
            patch);
            
            ACtrlComponent ctrl = param.CreateControl();
            zoomUI.setPresetComponent(ctrl);
            
            JPanel ctrlLayerPanel = new JPanel();
            JLayer<JComponent> ctrlLayer = new JLayer<JComponent>(ctrlLayerPanel, zoomUI);
            
            Dimension boundsOriginal = ctrl.getPreferredSize();
            Dimension bounds = ctrl.getPreferredSize();
            bounds.width *= scale;
            bounds.height *= scale;
            c.ipadx = bounds.width / 4;
            c.ipady = bounds.width / 4;
            ctrlLayer.setPreferredSize(bounds);
            ctrlLayers.add(ctrlLayer);
            ctrlLayerPanel.setLayout(null);
            ctrlLayerPanel.setPreferredSize(bounds);
            ctrlLayerPanel.add(ctrl);
            ctrl.setBounds(0, 0, boundsOriginal.width, boundsOriginal.height);
            ctrl.repaint();

            ctrls.add(ctrl);
            ctrl.addACtrlListener(ctrlListener);
            ctrl.addFocusListener(cbFocusListener);
            Preset p = param.GetPreset(i + 1);
            if (p != null) {
                cb.setSelected(true);
                ctrl.setValue(p.value.getDouble());
            } else {
                ctrl.setEnabled(false);
                ctrl.setValue(param.getValue().getDouble());
            }
            add(ctrlLayer, c);
        }
    }
    
    ActionListener cbActionListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            String[] s = e.getActionCommand().split(" ");
            int i = Integer.parseInt(s[1]) - 1;
            if (((JCheckBox) e.getSource()).isSelected()) {
                param.AddPreset(i + 1, param.getValue());
                ctrls.get(i).setEnabled(true);
                ctrls.get(i).setValue(param.GetPreset(i + 1).value.getDouble()); // TBC!!!
            } else {
                ctrls.get(i).setEnabled(false);
                param.RemovePreset(i + 1);
            }
            param.GetObjectInstance().patch.presetUpdatePending = true;
            ctrlLayers.get(i).repaint();
        }

    };
    
    FocusListener cbFocusListener = new FocusListener() {
        public void repaint() {
            for(JLayer layer : ctrlLayers) {
                layer.repaint();
            }
        }
        
        public void focusGained(FocusEvent e) {
            this.repaint();
        }
        
        public void focusLost(FocusEvent e) {
            this.repaint();
        }        
    };

    ACtrlListener ctrlListener = new ACtrlListener() {

        @Override
        public void ACtrlAdjusted(ACtrlEvent e) {
            int i = ctrls.indexOf(e.getSource());
            if (i >= 0) {
                if (ctrls.get(i).isEnabled()) {
                    if (param.getValue() instanceof ValueInt32) {
                        param.AddPreset(i + 1, new ValueInt32((int) ctrls.get(i).getValue()));
                    } else if (param.getValue() instanceof ValueFrac32) {
                        param.AddPreset(i + 1, new ValueFrac32(ctrls.get(i).getValue()));
                    }
                }
            }
            param.GetObjectInstance().patch.presetUpdatePending = true;
        }
    };
}
