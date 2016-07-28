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
import axoloti.utils.KeyUtils;
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
public class HRadioComponent extends ACtrlComponent {

    private double value;
    private final int n;
    private final int bsize = 12;

    public HRadioComponent(int value, int n) {
        //setInheritsPopupMenu(true);
        this.value = 0;//value;
        this.n = n;
        SetupTransferHandler();
    }

    private boolean dragAction = false;

    @Override
    protected void mouseDragged(MouseEvent e) {
        if (dragAction) {
            int i = e.getX() / bsize;
            if ((i >= 0) && (i < n)) {
                setValue(i);
            }
        }
    }

    @Override
    protected void mousePressed(MouseEvent e) {
        grabFocus();
        if (e.getButton() == 1) {
            int i = e.getX() / bsize;
            if ((i >= 0) && (i < n)) {
                setValue(i);
                dragAction = true;
            }
        }
    }

    @Override
    protected void mouseReleased(MouseEvent e) {
        dragAction = false;
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (KeyUtils.isIgnoreModifierDown(ke)) {
            return;
        }
        switch (ke.getKeyCode()) {
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_LEFT: {
                int v = (int) value - 1;
                if (v < 0) {
                    v = 0;
                }
                setValue(v);
                ke.consume();
                return;
            }
            case KeyEvent.VK_UP:
            case KeyEvent.VK_RIGHT: {
                int v = (int) value + 1;
                if (v >= n) {
                    v = n - 1;
                }
                setValue(v);
                ke.consume();
                return;
            }
            case KeyEvent.VK_HOME: {
                setValue(0);
                ke.consume();
                return;
            }
            case KeyEvent.VK_END: {
                setValue(n - 1);
                ke.consume();
                return;
            }
        }
        
        switch (ke.getKeyChar()) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                int i = ke.getKeyChar() - '0';
                if (i < n) {
                    setValue(i);
                }
                ke.consume();
        }
    }

    private static final Stroke strokeThin = new BasicStroke(1);
    private static final Stroke strokeThick = new BasicStroke(2);

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        if (isEnabled()) {
            g2.setColor(Theme.getCurrentTheme().Component_Secondary);
        } else {
            g2.setColor(Theme.getCurrentTheme().Object_Default_Background);
        }
        for (int i = 0; i < n; i++) {
            g2.fillOval(i * bsize, 0, bsize, bsize);
        }

        g2.setPaint(getForeground());
        if (isFocusOwner()) {
            g2.setStroke(strokeThick);
        }

        for (int i = 0; i < n; i++) {
            g2.drawOval(i * bsize, 0, bsize, bsize);
        }

        if (isEnabled()) {
            g2.fillOval((int) value * bsize + 2, 2, bsize - 3, bsize - 3);
        } else {
        }
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(bsize * n + 2, bsize + 2);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(bsize * n + 2, bsize + 2);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(bsize * n + 2, bsize + 2);
    }

    @Override
    public void setValue(double value) {
        if (this.value != value) {
            this.value = value;
        }
        fireEvent();
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    void keyReleased(KeyEvent ke) {
    }
}
