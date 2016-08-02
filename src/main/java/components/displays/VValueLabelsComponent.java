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
package components.displays;

import axoloti.utils.Constants;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;

/**
 *
 * @author Johannes Taelman
 */
public class VValueLabelsComponent extends JComponent {

    private final double max;
    private final double min;
    private final double tick;

    int height = 128;
    int width = 25;

    public VValueLabelsComponent(double min, double max, double tick) {
        this.max = max;
        this.min = min;
        this.tick = tick;
        Dimension d = new Dimension(width, height);
        setPreferredSize(d);
        setMaximumSize(d);
        setMinimumSize(d);
    }

    final int margin = 0;

    int ValToPos(double v) {
        return (int) (margin + ((max - v) * (height - 2 * margin)) / (max - min));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g2.setPaint(getForeground());
        int inset = 3;
        for (double v = min + tick; v < max; v += tick) {
            int y = ValToPos(v);
            g2.drawLine(width - inset, y, width, y);
            String s;
            if (Math.rint(v) == v) {
                s = String.format("%4.0f", v);
            } else {
                s = String.format("%4.1f", v);
            }
            g2.setFont(Constants.FONT);
            g2.drawString(s, 0, y + 4);
        }
    }
}
