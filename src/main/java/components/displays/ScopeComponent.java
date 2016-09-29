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
public class ScopeComponent extends ADispComponent {

    private final int length = 64;
    private final int vsize = 64;
    private final int[] value = new int[length];
    private final int[] xvalue = new int[length];
    private int index = 0;
    private final double max;
    private final double min;

    public ScopeComponent(double min, double max) {
        this.max = max;
        this.min = min;
        for (int i = 0; i < length; i++) {
            xvalue[i] = i + 1;
        }
        Dimension d = new Dimension(length + 2, vsize + 2);
        setMinimumSize(d);
        setMaximumSize(d);
        setPreferredSize(d);
    }
    private static final Stroke strokeThin = new BasicStroke(0.75f);
    private static final Stroke strokeThick = new BasicStroke(1.f);

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setStroke(strokeThick);
        g2.setColor(Theme.getCurrentTheme().Component_Secondary);
        g2.fillRect(0, 0, length + 2, vsize + 2);
        g2.setPaint(getForeground());
        g2.drawRect(0, 0, length + 2, vsize + 2);
        g2.setStroke(strokeThin);
        if (index > 1) {
            g2.drawPolyline(xvalue, value, index - 1);
        }
        g2.setColor(Theme.getCurrentTheme().Component_Mid);
        if (index < length - 2) {
            g2.drawPolyline(java.util.Arrays.copyOfRange(xvalue, index, length - 1),
                    java.util.Arrays.copyOfRange(value, index, length - 1), length - index - 1);
        }
        int v = (int) Project(0);
        g2.drawLine(0, v, length, v);
    }

    @Override
    public void setValue(double value) {
        if (value < min) {
            value = min;
        }
        if (value > max) {
            value = max;
        }
        this.value[index++] = (int) Project(value);
        if (index >= length) {
            index = 0;
        }
        repaint();
    }

    double Project(double value) {
        return (1 + (vsize * (max - value)) / ((max - min)));
    }

    public double getMinimum() {
        return min;
    }

    public double getMaximum() {
        return max;
    }
}
