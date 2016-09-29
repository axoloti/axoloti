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
public class VUComponent extends ADispComponent {

    private double value;
    private double accumvalue;
    private double peakaccumvalue;
    private static final int w = 6;
    private static final int h = 16;
    private static final Dimension dim = new Dimension(w, h);

    public VUComponent() {
        value = 0;
        setMinimumSize(dim);
        setPreferredSize(dim);
        setMaximumSize(dim);
        setSize(dim);
    }

    double decay = 0.5;

    @Override
    public void setValue(double value) {
        this.value = value / 256.0;
        double valuesq = this.value * this.value;
        accumvalue = (accumvalue * decay) + (valuesq * (1.0 - decay));

        double peakdecay = 0.75;
        peakaccumvalue = (peakaccumvalue * peakdecay) + (valuesq * (1.0 - peakdecay));
        // peak
        if (valuesq > peakaccumvalue) {
            peakaccumvalue = valuesq;
        }
        repaint();
    }

    int valueToPos(double v) {
        double dB = Math.log10(Math.abs(v) + 0.000000001);
        int i = (int) (-dB * 3);
        // 3 pixels per 10 dB
        // with h = 15 this is 50dB range
        if (i > h) {
            i = h;
        }
        return h - i;
    }

    static Color CDarkGreen = Theme.getCurrentTheme().VU_Dark_Green;
    static Color CDarkYellow = Theme.getCurrentTheme().VU_Dark_Yellow;
    static Color CDarkRed = Theme.getCurrentTheme().VU_Dark_Red;

    static Color CBrightGreen = Theme.getCurrentTheme().VU_Bright_Green;
    static Color CBrightYellow = Theme.getCurrentTheme().VU_Bright_Yellow;
    static Color CBrightRed = Theme.getCurrentTheme().VU_Bright_Red;

    static int segmentsRed = 2;
    static int segmentsYellow = 3;
    static int segmentsGreen = h - 5;

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);        
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g2.setPaint(CDarkRed);
        g2.fillRect(0, 0, w, segmentsRed);
        g2.setPaint(CDarkYellow);
        g2.fillRect(0, segmentsRed, w, segmentsYellow);
        g2.setPaint(CDarkGreen);
        g2.fillRect(0, segmentsYellow + segmentsRed, w, segmentsGreen);

        int pa = valueToPos(accumvalue);
        if (pa < segmentsGreen) {
            g2.setPaint(CBrightGreen);
            g2.fillRect(1, h - pa, w - 2, pa);
        } else {
            g2.setPaint(CBrightGreen);
            g2.fillRect(1, h - segmentsGreen, w - 2, segmentsGreen);
            if (pa < (segmentsYellow + segmentsGreen)) {
                g2.setPaint(CBrightYellow);
                g2.fillRect(1, h - pa, w - 2, pa - segmentsGreen);
            } else {
                g2.setPaint(CBrightYellow);
                g2.fillRect(1, segmentsRed, w - 2, segmentsYellow);
                g2.setPaint(CBrightRed);
                g2.fillRect(1, h - pa, w - 2, pa - segmentsYellow - segmentsGreen);
            }
        }
        int pp = valueToPos(peakaccumvalue);
        if (pp < segmentsGreen) {
            g2.setPaint(CBrightGreen);
        } else if (pp < (segmentsGreen + segmentsYellow)) {
            g2.setPaint(CBrightYellow);
        } else {
            g2.setPaint(CBrightRed);
        }
        g2.fillRect(1, h - pp, w - 2, 1);
    }

}
