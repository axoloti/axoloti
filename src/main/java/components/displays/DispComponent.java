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

import axoloti.Theme;
import axoloti.utils.Constants;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

/**
 *
 * @author Johannes Taelman
 */
public class DispComponent extends ADispComponent {

    private double value;
    private final double max;
    private final double min;
    boolean overflow = false;
    private static final Dimension dim = new Dimension(28, 32);

    public DispComponent(double value, double min, double max) {
        this.value = value;
        this.max = max;
        this.min = min;
        setPreferredSize(dim);
        setMaximumSize(dim);
        setMinimumSize(dim);
    }
    private static final Stroke strokeThin = new BasicStroke(0.5f);
    private static final Stroke strokeThick = new BasicStroke(1);

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        int tick = 1;
        int radius = Math.min(getSize().width, getSize().height) / 2 - tick;
        g2.setStroke(strokeThin);
        g2.setColor(Theme.getCurrentTheme().Component_Secondary);
        g2.setPaint(getForeground());
        int b = radius / 2;
        g.drawArc(b, b, 2 * (radius - b), 2 * (radius - b), -45, 270);
        double th = 0.75 * Math.PI + (value - min) * (1.5 * Math.PI) / (max - min);
        int x = (int) (Math.cos(th) * radius);
        int y = (int) (Math.sin(th) * radius);
        if (overflow) {
            g2.setColor(Theme.getCurrentTheme().Error_Text);
        }
        g2.setStroke(strokeThick);
        g2.drawLine(radius, radius, radius + x, radius + y);
        String s = String.format("%5.2f", value);
        g2.setFont(Constants.FONT);
        g2.drawString(s, 0, getSize().height);
    }

    @Override
    public void setValue(double value) {
        overflow = false;
        if (value < min) {
            value = min;
            overflow = true;
        }
        if (value > max) {
            value = max;
            overflow = true;
        }
        if (this.value != value) {
            this.value = value;
            repaint();
        }
    }

    public double getMinimum() {
        return min;
    }

    public double getMaximum() {
        return max;
    }
}
