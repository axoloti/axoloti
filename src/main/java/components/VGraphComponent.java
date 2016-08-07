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
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import javax.swing.JComponent;

/**
 *
 * @author Johannes Taelman
 */
public class VGraphComponent extends JComponent {

    private final int length;
    private final int vsize;
    private int[] ypoints;
    private int[] xpoints;
    private int index = 0;
    private double max;
    private double min;
    private int imax;
    private int imin;
    private int y0;

    public VGraphComponent(int length, int vsize, double min, double max) {
        this.length = length;
        this.vsize = vsize;
        this.max = max;
        this.min = min;
        this.imax = (int) max;
        this.imin = (int) min;
        this.xpoints = new int[length];
        this.ypoints = new int[length];
        for (int i = 0; i < length; i++) {
            xpoints[i] = i + 1;
            ypoints[i] = vsize;
        }
        y0 = valToPos(0);
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
        g2.setPaint(Theme.getCurrentTheme().Patch_Unlocked_Background);
        g2.drawLine(0, y0, length, y0);
        g2.setPaint(getForeground());
        g2.drawRect(0, 0, length + 2, vsize + 2);
        g2.setStroke(strokeThin);
        //if (index > 1) {
        g2.drawPolyline(xpoints, ypoints, length);
        //}
    }

    public int valToPos(int x) {
        if (x < imin) {
            x = imin;
        }
        if (x > imax) {
            x = imax;
        }
        return (int) Math.round((double) ((max - x) * vsize) / (max - min));
    }

    public void setValue(int value[]) {
        for (int i = 0; i < length; i++) {
            this.ypoints[i] = valToPos(value[i]);
        }
        repaint();
    }

    public double getMinimum() {
        return min;
    }

    public double getMaximum() {
        return max;
    }
}
