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

import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.preferences.Theme;
import axoloti.property.MidiCCProperty;
import axoloti.swingui.property.menu.AssignMidiCCMenuItems;
import axoloti.utils.Constants;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

/**
 *
 * @author Johannes Taelman
 */
public class AssignMidiCCComponent extends JComponent {

    private static final Dimension dim = new Dimension(16, 12);

    private final ParameterInstance parameterInstance;

    public AssignMidiCCComponent(ParameterInstance parameterInstance) {
        this.parameterInstance = parameterInstance;
        initComponents();
    }

    private void initComponents() {
        setMinimumSize(dim);
        setMaximumSize(dim);
        setPreferredSize(dim);
        setSize(dim);

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                doPopup();
                e.consume();
            }

        });
    }

    void doPopup() {
        JPopupMenu sub1 = new JPopupMenu();
        AssignMidiCCMenuItems assignMidiCCMenuItems = new AssignMidiCCMenuItems(parameterInstance, (MidiCCProperty) ParameterInstance.MIDI_CC);
        sub1.add(assignMidiCCMenuItems);
        sub1.show(this, 0, getHeight() - 1);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(Constants.FONT);
        g2.setColor(Theme.getCurrentTheme().Object_Default_Background);
        g2.fillRect(1, 1, getWidth(), getHeight());
        if (parameterInstance.getMidiCC() >= 0) {
            g2.setColor(Theme.getCurrentTheme().Component_Primary);
            g2.fillRect(1, 1, 8, getHeight());
            g2.setColor(Theme.getCurrentTheme().Component_Secondary);
        } else {
            g2.setColor(Theme.getCurrentTheme().Component_Primary);
        }
        g2.drawString("C", 1, getHeight() - 2);
        g2.setColor(Theme.getCurrentTheme().Component_Primary);
        final int rmargin = 2;
        final int htick = 2;
        int[] xp = new int[]{getWidth() - rmargin - htick * 2, getWidth() - rmargin, getWidth() - rmargin - htick};
        final int vmargin = 4;
        int[] yp = new int[]{vmargin, vmargin, vmargin + htick * 2};
        g2.fillPolygon(xp, yp, 3);
    }

    public void setCC(int i) {
        repaint();
    }
}
