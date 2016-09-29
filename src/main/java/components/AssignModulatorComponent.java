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

import axoloti.Theme;
import axoloti.parameters.ParameterInstanceFrac32UMap;
import axoloti.utils.Constants;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

/**
 *
 * @author Johannes Taelman
 */
public class AssignModulatorComponent extends JComponent {

    private static final Dimension dim = new Dimension(16, 12);

    final ParameterInstanceFrac32UMap param;

    public AssignModulatorComponent(ParameterInstanceFrac32UMap param) {
        setMinimumSize(dim);
        setMaximumSize(dim);
        setPreferredSize(dim);
        setSize(dim);
        this.param = param;

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                doPopup();
                e.consume();
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }

    void doPopup() {
        JPopupMenu sub2 = new JPopupMenu();
        AssignModulatorMenuItems mi = new AssignModulatorMenuItems(param, sub2);
        sub2.show(this, 0, getHeight() - 1);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (param.getModulators() != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setFont(Constants.FONT);
            g2.setColor(Theme.getCurrentTheme().Object_Default_Background);
            g2.fillRect(1, 1, getWidth(), getHeight());
            if (param.getModulators() != null) {
                g2.setColor(Theme.getCurrentTheme().Component_Primary);
                g2.fillRect(1, 1, 8, getHeight());
                g2.setColor(Theme.getCurrentTheme().Component_Secondary);
            } else {
                g2.setColor(Theme.getCurrentTheme().Component_Primary);
            }
            g2.drawString("M", 1, getHeight() - 2);
            g2.setColor(Theme.getCurrentTheme().Component_Primary);
            final int rmargin = 2;
            final int htick = 2;
            int[] xp = new int[]{getWidth() - rmargin - htick * 2, getWidth() - rmargin, getWidth() - rmargin - htick};
            final int vmargin = 4;
            int[] yp = new int[]{vmargin, vmargin, vmargin + htick * 2};
            g2.fillPolygon(xp, yp, 3);
        }
    }

}
