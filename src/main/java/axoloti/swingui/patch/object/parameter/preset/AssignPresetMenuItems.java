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

import axoloti.patch.object.parameter.ParameterInstanceController;
import axoloti.patch.object.parameter.preset.Preset;
import axoloti.swingui.patch.object.parameter.ParameterInstanceView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 *
 * @author Johannes Taelman
 */
public class AssignPresetMenuItems {

    final ParameterInstanceView param;
    final JComponent parent;

    public AssignPresetMenuItems(ParameterInstanceView param, JComponent parent) {
        this.param = param;
        this.parent = parent;

        //sub2 = new JPopupMenu();
        {
            JMenuItem mi = new JMenuItem("include in current preset");
            mi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int i = param.getPresetEditActive();
                    if (i == 0) {
                        return;
                    }
                    param.getDModel().getController().addMetaUndo("include in current preset");
                    param.getDModel().getController().addPreset(i, param.getDModel().getValue());
                }
            });
            int i = param.getPresetEditActive();
            if (param.getDModel().getPreset(i) != null) {
                mi.setEnabled(false);
            }
            parent.add(mi);
        }
        {
            JMenuItem mi = new JMenuItem("exclude from current preset");
            mi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int i = param.getPresetEditActive();
                    if (i == 0) {
                        return;
                    }
                    param.getDModel().getController().addMetaUndo("exclude from current preset");
                    param.getDModel().getController().removePreset(i);
                }
            });
            int i = param.getPresetEditActive();
            if (param.getDModel().getPreset(i) == null) {
                mi.setEnabled(false);
            }
            parent.add(mi);
        }
        {
            JMenuItem mi = new JMenuItem("clear all presets");
            mi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    List<Preset> presets = AssignPresetMenuItems.this.param.getDModel().getPresets();
                    ParameterInstanceController c = AssignPresetMenuItems.this.param.getDModel().getController();
                    c.addMetaUndo("clear all presets of parameter");
                    for (Preset p : presets) {
                        c.removePreset(p.getIndex());
                    }
                }
            });
            if (param.getDModel().getPresets().isEmpty()) {
                mi.setEnabled(false);
            }
            parent.add(mi);
        }

        JPanel panel = new AssignPresetPanel(param);
        parent.add(panel);
    }

}
