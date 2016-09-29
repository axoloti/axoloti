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
package components.control;

import axoloti.Theme;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Johannes Taelman
 */
public class HSliderComponent extends ACtrlComponent {

    double value = 0;
    double max = 128;
    double min = -128;
    int px;

    public HSliderComponent() {
        Dimension d = new Dimension(160, 12);
        setMinimumSize(d);
        setPreferredSize(d);
        setMaximumSize(d);
        setSize(d);
        SetupTransferHandler();
    }

    @Override
    protected void mouseDragged(MouseEvent e) {
        setValue(value - px + e.getX());
        px = e.getX();
    }

    @Override
    protected void mousePressed(MouseEvent e) {
        px = e.getX();
        e.consume();
        fireEventAdjustmentBegin();
    }

    @Override
    public void keyPressed(KeyEvent ke) {
    }

    @Override
    void keyReleased(KeyEvent ke) {
    }

    @Override
    protected void mouseReleased(MouseEvent e) {
        if (!e.isPopupTrigger()) {
            fireEventAdjustmentFinished();
            e.consume();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        int margin = 50;
        int bwidth = getWidth() - margin;
        g2.setPaint(Theme.getCurrentTheme().Component_Secondary);
        g2.drawRect(0, 0, bwidth, getHeight() - 1);
        g2.setPaint(getForeground());
        g2.drawRect(0, 0, bwidth, getHeight() - 1);
        int p = (int) (1 + ((value - min) * (bwidth - 2)) / (max - min));
        int p1 = (int) (1 + ((0 - min) * (bwidth - 2)) / (max - min));
        g2.drawLine(p, getHeight() / 2, p1, getHeight() / 2);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(p, 0, p, getHeight());
        String s = String.format("%5.2f", value);
        Rectangle2D r = g2.getFontMetrics().getStringBounds(s, g);
        g2.drawString(s, bwidth + (margin / 2) - (int) (0.5 + r.getWidth() / 2), getHeight());
    }

    @Override
    public void setValue(double value) {
        if (value > max) {
            value = max;
        }
        if (value < min) {
            value = min;
        }
        this.value = value;
        repaint();
        fireEvent();
    }

    @Override
    public double getValue() {
        return value;
    }

    public void setMinimum(double min) {
        this.min = min;
    }

    public double getMinimum() {
        return min;
    }

    public void setMaximum(double max) {
        this.max = max;
    }

    public double getMaximum() {
        return max;
    }
}
