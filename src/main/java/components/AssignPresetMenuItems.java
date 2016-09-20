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

import axoloti.parameters.ParameterInstance;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 *
 * @author Johannes Taelman
 */
public class AssignPresetMenuItems {

    final ParameterInstance param;
    final JComponent parent;

    public AssignPresetMenuItems(ParameterInstance param, JComponent parent) {
        this.param = param;
        this.parent = parent;

        //sub2 = new JPopupMenu();
        {
            JMenuItem mi = new JMenuItem("include in current preset");
            mi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    AssignPresetMenuItems.this.param.IncludeInPreset();
                }
            });
            parent.add(mi);
        }
        {
            JMenuItem mi = new JMenuItem("exclude from current preset");
            mi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    AssignPresetMenuItems.this.param.ExcludeFromPreset();
                }
            });
            parent.add(mi);
        }
        {
            JMenuItem mi = new JMenuItem("clear all presets");
            mi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (AssignPresetMenuItems.this.param.getPresets() != null) {
                        AssignPresetMenuItems.this.param.getPresets().clear();
                    }
                }
            });
            parent.add(mi);
        }

        JPanel panel = new AssignPresetPanel(param);
        parent.add(panel);
    }

}
