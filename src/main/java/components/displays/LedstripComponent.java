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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 *
 * @author Johannes Taelman
 */
public class LedstripComponent extends ADispComponent {

    private double value;
    private final int n;
    private static final int bsize = 12;

    public LedstripComponent(int value, int n) {
        //setInheritsPopupMenu(true);
        this.value = 0;//value;
        this.n = n;
        Dimension d = new Dimension(bsize * n + 2, bsize + 2);
        setMinimumSize(d);
        setPreferredSize(d);
        setMaximumSize(d);
        setSize(d);
    }

    final Color c_off = Theme.getCurrentTheme().Led_Strip_Off;
    final Color c_on = Theme.getCurrentTheme().Led_Strip_On;

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setColor(Theme.getCurrentTheme().Object_Default_Background);
        g2.fillRect(0, 0, bsize * n + 1, bsize + 1);
//        g2.setPaint(getForeground());
//        g2.drawRect(0, 0, bsize * n + 1, bsize + 1);
//        for (int i = 1; i < n; i++) {
//            g2.drawLine(bsize * i, 0, bsize * i, bsize + 1);
//        }

        int v = (int) value;
        int inset = 3;
        for (int i = 0; i < n; i++) {
            if ((v & 1) != 0) {
                g2.setColor(c_on);
            } else {
                g2.setColor(c_off);
            }
            g2.fillRect(i * bsize + inset, inset, bsize - inset - 1, bsize - inset);
            v = v >> 1;
        }
    }

    @Override
    public void setValue(double value) {
        if (this.value != value) {
            this.value = value;
            repaint();
        }
    }
}
