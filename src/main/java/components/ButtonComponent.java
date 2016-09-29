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
import axoloti.utils.Constants;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

/**
 *
 * @author Johannes Taelman
 */
public class ButtonComponent extends JComponent implements MouseInputListener, KeyListener {

    boolean isHighlighted = false;
    String label;

    public interface ActListener {

        public void OnPushed();
    }
    ArrayList<ActListener> actListeners = new ArrayList<ActListener>();

    public void addActListener(ActListener al) {
        actListeners.add(al);
    }

    void DoPushed() {
        if (isEnabled()) {
            for (ActListener al : actListeners) {
                al.OnPushed();
            }
        }
    }

    void setHighlighted(boolean highlighted) {
        if (!isEnabled()) {
            highlighted = false;
        }
        if (isHighlighted != highlighted) {
            isHighlighted = highlighted;
            repaint();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setFocusable(enabled);
        repaint();
    }

    public ButtonComponent(String label) {
        this.label = label;
        FontRenderContext frc = new FontRenderContext(null, true, true);
        int width = 0;
        TextLayout tl = new TextLayout(label, Constants.FONT, frc);
        width = (int) tl.getBounds().getWidth();
        if (width < 20) {
            width = 20;
        }
        Dimension d = new Dimension(width + 10, 18);
        setSize(d);
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(new Dimension(5000, 18));
        setFocusable(true);
        addMouseListener(this);
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_SPACE) {
            ke.consume();
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_SPACE) {
            ke.consume();
        }
    }
    private static final Stroke strokeThin = new BasicStroke(1);
    private static final Stroke strokeThick = new BasicStroke(2);

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        final int radius = 12;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        if (isFocusOwner()) {
            g2.setStroke(strokeThick);
        } else {
            g2.setStroke(strokeThin);
        }
        if (isHighlighted) {
            g2.setPaint(getForeground());
            g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, radius, radius);
            g2.setPaint(Theme.getCurrentTheme().Component_Secondary);
            g2.setFont(Constants.FONT);
            g2.drawString(label, 8, getHeight() - 5);
        } else {
            if (isEnabled()) {
                g2.setPaint(Theme.getCurrentTheme().Component_Secondary);
            } else {
                g2.setPaint(Theme.getCurrentTheme().Object_Default_Background);
            }
            g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, radius, radius);
            g2.setPaint(getForeground());
            g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, radius, radius);
            g2.setFont(Constants.FONT);
            g2.drawString(label, 8, getHeight() - 5);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        grabFocus();
        DoPushed();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
        isHighlighted = false;
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        setHighlighted(true);
        e.consume();
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        setHighlighted(false);
        e.consume();
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (contains(e.getX(), getY())) {
            setHighlighted(true);
        } else {
            setHighlighted(false);
        }
        e.consume();
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
