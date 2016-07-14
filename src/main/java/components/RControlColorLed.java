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
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;

/**
 *
 * @author Johannes Taelman
 */
public class RControlColorLed extends JComponent {

    Color color = Theme.getCurrentTheme().Component_Secondary;

    public void setColor(Color color) {
        if (this.color != color) {
            this.color = color;
            repaint();
        }
    }

    @Override
    public void paint(Graphics g) {
        int height = getHeight();
        int width = getWidth();

        int diameter = (height > width ? width : height) - 2;
        int hoffset = (width - diameter) / 2;
        int voffset = (height - diameter) / 2;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(Theme.getCurrentTheme().Object_Default_Background);
        g2.fillRect(0, 0, width, height);
        g2.setPaint(Theme.getCurrentTheme().Component_Secondary);
        g2.drawOval(hoffset, voffset, diameter, diameter);
        g2.setPaint(color);
        g2.fillOval(hoffset, voffset, diameter, diameter);
    }
}
