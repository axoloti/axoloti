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

import axoloti.dialogs.MidiAssignments;
import axoloti.parameters.ParameterInstance;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 *
 * @author Johannes Taelman
 */
public class AssignMidiCCMenuItems {

    public AssignMidiCCMenuItems(final ParameterInstance param, JComponent parent) {
        JMenuItem m = new JMenuItem("Assign...");
        m.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MidiAssignments ma = new MidiAssignments(null, true, param);
                int v = ma.getResult();
                if (v>=0) {
                    param.actionPerformed(new ActionEvent(this, 0, "CC" + v));
                } else {
                    param.actionPerformed(new ActionEvent(this, 0, "none"));                    
                }
            }
        });
        parent.add(m);
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(16, 0));
        ButtonGroup group = new ButtonGroup();
        int cc = param.getMidiCC();
        parent.add(p);
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 8; j++) {
                int k = i + j * 16;
                if (k != 0) {
                    JRadioButton rbMenuItem = new JRadioButton("CC" + k);
                    rbMenuItem.setActionCommand("CC" + k);
                    rbMenuItem.addActionListener(param);
                    group.add(rbMenuItem);
                    if (cc == k) {
                        rbMenuItem.setSelected(true);
                    }
                    p.add(rbMenuItem);
                } else {
                    JRadioButton rbMenuItem = new JRadioButton("none");
                    rbMenuItem.setActionCommand("none");
                    group.add(rbMenuItem);
                    if (cc < 0) {
                        rbMenuItem.setSelected(true);
                    }
                    rbMenuItem.addActionListener(param);
                    p.add(rbMenuItem);
                }
            }
        }
    }
}
