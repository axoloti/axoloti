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
package axoloti.swingui.components.control;

import axoloti.preferences.Preferences;
import axoloti.preferences.Theme;
import axoloti.swingui.TransparentCursor;
import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Stroke;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 *
 * @author Johannes Taelman
 */
public class VSliderComponent extends ACtrlComponent {

    private double value;
    private double max;
    private double min;
    private double tick;

    private static final int height = 128;
    private static final int width = 12;
    private static final Dimension dim = new Dimension(width, height);
    private String keybBuffer = "";
    private Robot robot;

    public VSliderComponent(double value, double min, double max, double tick) {
        super();
        this.max = max;
        this.min = min;
        this.value = value;
        this.tick = tick;
        initComponent();
    }

    private void initComponent() {
        setSize(dim);
        setPreferredSize(dim);
        setMaximumSize(dim);
        setMinimumSize(dim);
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                keybBuffer = "";
            }

            @Override
            public void focusLost(FocusEvent e) {
                keybBuffer = "";
            }
        });
        setupTransferHandler();
    }
    private int px;
    private int py;

    @Override
    protected void mouseDragged(MouseEvent e) {
        if (isEnabled()) {
            double v = value + tick * ((int) Math.round((py - e.getYOnScreen())));
            robotMoveToCenter();
            if (robot == null) {
                py = e.getYOnScreen();
            }
            fireValue(v);
        }
    }

    @Override
    protected void mousePressed(MouseEvent e) {
        if (!e.isPopupTrigger()) {
            grabFocus();
            px = e.getXOnScreen();
            py = e.getYOnScreen();
            robot = createRobot();
            if (!Preferences.getPreferences().getMouseDoNotRecenterWhenAdjustingControls()) {
                getRootPane().setCursor(TransparentCursor.get());
            }
            e.consume();
            fireEventAdjustmentBegin();
        }
    }

    @Override
    protected void mouseReleased(MouseEvent e) {
        if (!e.isPopupTrigger()) {
            fireEventAdjustmentFinished();
        }
        getRootPane().setCursor(Cursor.getDefaultCursor());
        robot = null;
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        double steps = tick;
        if (ke.isShiftDown()) {
            steps = 8 * tick;
        }
        switch (ke.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_RIGHT:
                fireEventAdjustmentBegin();
                fireValue(getValue() + steps);
                ke.consume();
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_LEFT:
                fireEventAdjustmentBegin();
                fireValue(getValue() - steps);
                ke.consume();
                break;
            case KeyEvent.VK_PAGE_UP:
                fireEventAdjustmentBegin();
                fireValue(getValue() + 5 * steps);
                ke.consume();
                break;
            case KeyEvent.VK_PAGE_DOWN:
                fireEventAdjustmentBegin();
                fireValue(getValue() - 5 * steps);
                ke.consume();
                break;
            case KeyEvent.VK_HOME:
                fireEventAdjustmentBegin();
                fireValue(max);
                fireEventAdjustmentFinished();
                ke.consume();
                break;
            case KeyEvent.VK_END:
                fireEventAdjustmentBegin();
                fireValue(min);
                fireEventAdjustmentFinished();
                ke.consume();
                break;
            case KeyEvent.VK_ENTER:
                fireEventAdjustmentBegin();
                try {
                    fireValue(Float.parseFloat(keybBuffer));
                } catch (java.lang.NumberFormatException ex) {
                }
                fireEventAdjustmentFinished();
                keybBuffer = "";
                ke.consume();
                repaint();
                break;
            case KeyEvent.VK_BACK_SPACE:
                if (keybBuffer.length() > 0) {
                    keybBuffer = keybBuffer.substring(0, keybBuffer.length() - 1);
                }
                ke.consume();
                repaint();
                break;
            case KeyEvent.VK_ESCAPE:
                keybBuffer = "";
                ke.consume();
                repaint();
                break;
            default:
        }
        switch (ke.getKeyChar()) {
            case '-':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case '0':
            case '.':
                keybBuffer += ke.getKeyChar();
                ke.consume();
                repaint();
                break;
            default:
        }
    }

    @Override
    void keyReleased(KeyEvent ke) {
        if (isEnabled()) {
            switch (ke.getKeyCode()) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_PAGE_UP:
                case KeyEvent.VK_PAGE_DOWN:
                    fireEventAdjustmentFinished();
                    ke.consume();
                    break;
                default:
            }
        }
    }

    final int margin = 2;

    int valToPos(double v) {
        return (int) (margin + ((max - v) * (height - 2 * margin)) / (max - min));
    }

    private static final Stroke strokeThin = new BasicStroke(1);
    private static final Stroke strokeThick = new BasicStroke(2);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        if (isEnabled()) {
            g2.setPaint(Theme.getCurrentTheme().Component_Secondary);
            g2.fillRect(0, 0, getWidth(), height);
            g2.setColor(getBackground());
            g2.drawRect(1, 1, getWidth() - 2, height - 2);
            g2.setPaint(getForeground());
            if (isFocusOwner()) {
                g2.setStroke(strokeThick);
            } else {
                g2.setStroke(strokeThin);
            }
            g2.drawRect(0, 0, getWidth(), height);
            int p = valToPos(value);
            int p1 = valToPos(0);
            //        g2.drawLine(1, p, 1, p1);
            //        g2.drawLine(width -1, p, width -1, p1);
            if (p1 - p > 0) {
                g2.fillRect(3, p, width - 5, p1 - p + 1);
            } else {
                g2.fillRect(3, p1, width - 5, p - p1 + 1);
            }

            g2.setStroke(strokeThin);
            g2.drawLine(0, p, getWidth(), p);
            //String s = String.format("%5.2f", value);
            //Rectangle2D r = g2.getFontMetrics().getStringBounds(s, g);
            //g2.drawString(s, bwidth+(margin/2)-(int)(0.5 + r.getWidth()/2), getHeight());
        } else {
            g2.setPaint(Theme.getCurrentTheme().Object_Default_Background);
            g2.fillRect(0, 0, getWidth(), height);
            g2.setPaint(getForeground());
            g2.setStroke(strokeThin);
            g2.drawRect(0, 0, getWidth(), height);
        }
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
        setToolTipText("" + value);
        repaint();
    }

    public void fireValue(double value) {
        setValue(value);
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

    @Override
    public void robotMoveToCenter() {
        if (robot != null) {
            getRootPane().setCursor(TransparentCursor.get());
            robot.mouseMove(px, py);
        }
    }
}
