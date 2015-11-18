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

import axoloti.MainFrame;
import axoloti.datatypes.ValueFrac32;
import axoloti.realunits.NativeToReal;
import axoloti.utils.Constants;
import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Robot;

/**
 *
 * @author Johannes Taelman
 */
public class DialComponent extends ACtrlComponent {

    private double value;
    private double max;
    private double min;
    private double tick;
    private NativeToReal convs[];
    private String keybBuffer = "";

    public void setNative(NativeToReal convs[]) {
        this.convs = convs;
    }

    public DialComponent(double value, double min, double max, double tick) {
        setInheritsPopupMenu(true);
        this.value = value;
        this.min = min;
        this.max = max;
        this.tick = tick;
        Dimension d = new Dimension(28, 32);
        setPreferredSize(d);
        setMaximumSize(d);
        setMinimumSize(d);
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

        SetupTransferHandler();
    }
    final int layoutTick = 3;
    Robot robot = null;

    @Override
    protected void mouseDragged(MouseEvent e) {
        if (isEnabled()) {
            double v;
            if ((MousePressedBtn == MouseEvent.BUTTON1)) {
                if (MainFrame.prefs.getMouseDialAngular()) {
                    int y = e.getY();
                    int x = e.getX();
                    int radius = Math.min(getSize().width, getSize().height) / 2 - layoutTick;
                    double th = Math.atan2(x - radius, radius - y);
                    v = min + (max - min) * (th + 0.75 * Math.PI) / (1.5 * Math.PI);
                    if (!e.isShiftDown()) {
                        v = Math.round(v / tick) * tick;
                    }
                } else {
                    double t = tick;
                    if (e.isShiftDown()) {
                        t = t * 0.1;
                    }
                    if (e.isControlDown()) {
                        t = t * 0.1;
                    }
                    v = value + t * (MousePressedCoordY - e.getYOnScreen());
                    if (robot == null) {
                        try {
                            robot = new Robot();
                        } catch (AWTException ex) {
                            Logger.getLogger(DialComponent.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    robot.mouseMove(MousePressedCoordX, MousePressedCoordY);
                }
                if (v > max) {
                    v = max;
                }
                if (v < min) {
                    v = min;
                }
                setValue(v);
            }
        }
    }
    int MousePressedCoordX = 0;
    int MousePressedCoordY = 0;
    int MousePressedBtn = 0;

    @Override
    protected void mousePressed(MouseEvent e) {
        if (isEnabled()) {
            grabFocus();
            MousePressedCoordX = e.getXOnScreen();
            MousePressedCoordY = e.getYOnScreen();
            MousePressedBtn = e.getButton();
            getRootPane().setCursor(MainFrame.transparentCursor);
        }
    }

    @Override
    protected void mouseReleased(MouseEvent e) {
        getRootPane().setCursor(Cursor.getDefaultCursor());
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (isEnabled()) {
            double steps = tick;
            if (ke.isShiftDown()) {
                steps = steps * 0.1; // mini steps!
                if (ke.isControlDown()) {
                    steps = steps * 0.1; // micro steps!                
                }
            } else if (ke.isControlDown()) {
                steps = steps * 10.0; //accelerate!
            }
            switch (ke.getKeyCode()) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_RIGHT:
                    setValue(getValue() + steps);
                    ke.consume();
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_LEFT:
                    setValue(getValue() - steps);
                    ke.consume();
                    break;
                case KeyEvent.VK_PAGE_UP:
                    setValue(getValue() + 5 * steps);
                    ke.consume();
                    break;
                case KeyEvent.VK_PAGE_DOWN:
                    setValue(getValue() - 5 * steps);
                    ke.consume();
                    break;
                case KeyEvent.VK_HOME:
                    setValue(getMin());
                    ke.consume();
                    break;
                case KeyEvent.VK_END:
                    setValue(getMax());
                    ke.consume();
                    break;
                case KeyEvent.VK_ENTER:
                    try {
                        setValue(Float.parseFloat(keybBuffer));
                    } catch (java.lang.NumberFormatException ex) {
                    }
                    keybBuffer = "";
                    ke.consume();
                    repaint();
                    break;
                case KeyEvent.VK_BACK_SPACE:
                    keybBuffer = keybBuffer.substring(0, keybBuffer.length() - 1);
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
    }

    @Override
    void keyReleased(KeyEvent ke) {
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
        int radius = Math.min(getSize().width, getSize().height) / 2 - layoutTick;
        g2.setPaint(getForeground());
        g2.drawLine(radius, radius, 0, 2 * radius);
        g2.drawLine(radius, radius, 2 * radius, 2 * radius);
        if (isFocusOwner()) {
            g2.setStroke(strokeThick);
        } else {
            g2.setStroke(strokeThin);
        }
        if (isEnabled()) {
            g2.setColor(Color.white);
        } else {
            g2.setColor(getBackground());
        }
        g2.fillOval(1, 1, radius * 2 - 2, radius * 2 - 2);
        g2.setPaint(getForeground());
        g2.drawOval(1, 1, radius * 2 - 2, radius * 2 - 2);
        if (isEnabled()) {
            double th = 0.75 * Math.PI + (value - min) * (1.5 * Math.PI) / (max - min);
            int x = (int) (Math.cos(th) * radius),
                    y = (int) (Math.sin(th) * radius);
            g2.drawLine(radius, radius, radius + x, radius + y);
            if (keybBuffer.isEmpty()) {
                String s = String.format("%5.2f", value);
                g2.setFont(Constants.font);
                g2.drawString(s, 0, getSize().height);
            } else {
                g2.setColor(Color.red);
                g2.setFont(Constants.font);
                g2.drawString(keybBuffer, 0, getSize().height);
            }
        }
    }

    @Override
    public void setValue(double value) {
        if (value < min) {
            value = min;
        }
        if (value > max) {
            value = max;
        }
        this.value = value;

        if (convs != null) {
            Point p = getParent().getLocationOnScreen();
            String s = "<html>";
            for (NativeToReal c : convs) {
                s += c.ToReal(new ValueFrac32(value)) + "<br>";
            }
            this.setToolTipText(s);
        }

        repaint();
        fireEvent();
    }

    @Override
    public double getValue() {
        return value;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getTick() {
        return tick;
    }

    public void setTick(double tick) {
        this.tick = tick;
    }
}
