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
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 *
 * @author Johannes Taelman
 */
public class PulseButtonComponent extends ACtrlComponent {

    private double value;
    private final static int bsize = 15;
    private final static Dimension dim = new Dimension(bsize, bsize);

    public PulseButtonComponent() {
        this.value = 0;
        setMinimumSize(dim);
        setMaximumSize(dim);
        setPreferredSize(dim);
        setSize(dim);
    }

    @Override
    protected void mouseDragged(MouseEvent e) {
    }

    @Override
    protected void mousePressed(MouseEvent e) {
        grabFocus();
        if (e.getButton() == 1) {
            setValue(1.0);
        }
    }

    @Override
    protected void mouseReleased(MouseEvent e) {
        if (e.getButton() == 1) {
            if (!e.isShiftDown()) {
                setValue(0.0);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_SPACE) {
            setValue(1.0);
            ke.consume();
        }
    }

    @Override
    void keyReleased(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_SPACE) {
            setValue(1.0);
            ke.consume();
        }
    }

    private static final Stroke strokeThin = new BasicStroke(1);
    private static final Stroke strokeThick = new BasicStroke(2);

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        if (isFocusOwner()) {
            g2.setStroke(strokeThick);
        } else {
            g2.setStroke(strokeThin);
        }
        int v = (int) value;
        if (v > 0.0) {
            g2.setPaint(getForeground());
            g2.drawOval(2, 2, bsize - 5, bsize - 5);
            g2.fillOval(2, 2, bsize - 5, bsize - 5);
        } else {
            g2.setColor(Theme.getCurrentTheme().Component_Secondary);
            g2.fillOval(2, 2, bsize - 5, bsize - 5);
            g2.setPaint(getForeground());
            g2.drawOval(2, 2, bsize - 5, bsize - 5);
        }
    }

    @Override
    public void setValue(double value) {
        if (this.value != value) {
            this.value = value;
        }
        fireEvent();
        repaint();
    }

    @Override
    public double getValue() {
        return value;
    }
}
