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
import axoloti.Theme;
import axoloti.datatypes.ValueFrac32;
import axoloti.realunits.NativeToReal;
import axoloti.utils.Constants;
import axoloti.utils.KeyUtils;
import axoloti.utils.Preferences;
import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Stroke;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Johannes Taelman
 */
// FIXME: integer versus float model. Currently NumberBoxComponent assumes integers...
public class NumberBoxComponent extends ACtrlComponent {

    private double value;
    private double max;
    private double min;
    private double tick;
    private NativeToReal convs[];
    private String keybBuffer = "";

    private boolean hiliteUp = false;
    private boolean hiliteDown = false;
    private boolean dragging = false;

    private Robot robot;

    int rmargin = 5;
    int htick = 3;

    public void setNative(NativeToReal convs[]) {
        this.convs = convs;
    }

    public NumberBoxComponent(double value, double min, double max, double tick) {
        this(value, min, max, tick, 50, 16);
    }

    public NumberBoxComponent(double value, double min, double max, double tick, int hsize, int vsize) {
        setInheritsPopupMenu(true);
        this.value = value;
        this.min = min;
        this.max = max;
        this.tick = tick;
        Dimension d = new Dimension(hsize, vsize);
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

    @Override
    protected void mouseDragged(MouseEvent e) {
        if (isEnabled() && dragging) {
            double v;
            if ((MousePressedBtn == MouseEvent.BUTTON1)) {
                double t = tick;
                t = t * 0.1;
                if (e.isShiftDown()) {
                    t = t * 0.1;
                }
                if (KeyUtils.isControlOrCommandDown(e)) {
                    t = t * 0.1;
                }
                v = value + t * (MousePressedCoordY - e.getYOnScreen());
                this.robotMoveToCenter();
                if (robot == null) {
                    MousePressedCoordY = e.getYOnScreen();
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
        if (!e.isPopupTrigger()) {
            robot = createRobot();
            grabFocus();
            if (isEnabled() && (e.getX() >= getWidth() - rmargin - htick * 2)) {
                dragging = false;
                if (e.getY() > getHeight() / 2) {
                    hiliteDown = true;
                    fireEventAdjustmentBegin();
                    setValue(value - tick);
                    fireEventAdjustmentFinished();
                } else {
                    hiliteUp = true;
                    fireEventAdjustmentBegin();
                    setValue(value + tick);
                    fireEventAdjustmentFinished();
                }
            } else {
                dragging = true;
                MousePressedCoordX = e.getXOnScreen();
                MousePressedCoordY = e.getYOnScreen();
                MousePressedBtn = e.getButton();
                if (!Preferences.LoadPreferences().getMouseDoNotRecenterWhenAdjustingControls()) {
                    getRootPane().setCursor(MainFrame.transparentCursor);
                }
                fireEventAdjustmentBegin();
            }
            e.consume();
        }
    }

    @Override
    protected void mouseReleased(MouseEvent e) {
        if (!e.isPopupTrigger()) {
            if (hiliteDown) {
                hiliteDown = false;
                repaint();
            } else if (hiliteUp) {
                hiliteUp = false;
                repaint();
            } else {
                fireEventAdjustmentFinished();
            }
            e.consume();
        }
        getRootPane().setCursor(Cursor.getDefaultCursor());
        robot = null;
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (isEnabled()) {
            double steps = tick;
            if (ke.isShiftDown()) {
                steps = steps * 0.1; // mini steps!
                if (KeyUtils.isControlOrCommandDown(ke)) {
                    steps = steps * 0.1; // micro steps!                
                }
            } else if (KeyUtils.isControlOrCommandDown(ke)) {
                steps = steps * 10.0; //accelerate!
            }
            switch (ke.getKeyCode()) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_RIGHT:
                    fireEventAdjustmentBegin();
                    setValue(getValue() + steps);
                    ke.consume();
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_LEFT:
                    fireEventAdjustmentBegin();
                    setValue(getValue() - steps);
                    ke.consume();
                    break;
                case KeyEvent.VK_PAGE_UP:
                    fireEventAdjustmentBegin();
                    setValue(getValue() + 5 * steps);
                    ke.consume();
                    break;
                case KeyEvent.VK_PAGE_DOWN:
                    fireEventAdjustmentBegin();
                    setValue(getValue() - 5 * steps);
                    ke.consume();
                    break;
                case KeyEvent.VK_HOME:
                    fireEventAdjustmentBegin();
                    setValue(getMin());
                    fireEventAdjustmentFinished();
                    ke.consume();
                    break;
                case KeyEvent.VK_END:
                    fireEventAdjustmentBegin();
                    setValue(getMax());
                    fireEventAdjustmentFinished();
                    ke.consume();
                    break;
                case KeyEvent.VK_ENTER:
                    fireEventAdjustmentBegin();
                    try {
                        setValue(Float.parseFloat(keybBuffer));
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
        g2.setPaint(getForeground());
        if (isFocusOwner()) {
            g2.setStroke(strokeThick);
        } else {
            g2.setStroke(strokeThin);
        }
        if (isEnabled()) {
            g2.setColor(Theme.getCurrentTheme().Component_Secondary);
        } else {
            g2.setPaint(Theme.getCurrentTheme().Object_Default_Background);
        }

        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setPaint(getForeground());
        g2.drawRect(0, 0, getWidth(), getHeight());

        String s;
        int h = 3;
        int v = 4;
        if (getWidth() < 20) {
            s = String.format("%d", (int) value);
            if (s.length() < 2) {
                h = 3;
            } else {
                h = 0;
            }
        } else {
            s = String.format("%5d", (int) value);
        }
        if (getHeight() < 15) {
            v = 2;
        }
        if (keybBuffer.isEmpty()) {
            g2.setFont(Constants.FONT);
            g2.drawString(s, h, getSize().height - v);
        } else {
            g2.setColor(Theme.getCurrentTheme().Error_Text);
            g2.setFont(Constants.FONT);
            g2.drawString(keybBuffer, h, getSize().height - v);
        }

        if (getWidth() < 20) {
            rmargin = -1;
            htick = 1;
        }
        g2.setStroke(strokeThin);
        {
            int[] xp = new int[]{getWidth() - rmargin - htick * 2, getWidth() - rmargin, getWidth() - rmargin - htick};
            final int vmargin = getHeight() - htick - 3;
            int[] yp = new int[]{vmargin, vmargin, vmargin + htick};
            if (hiliteDown) {
                g2.drawPolygon(xp, yp, 3);
            } else {
                g2.fillPolygon(xp, yp, 3);
            }
        }
        {
            int[] xp = new int[]{getWidth() - rmargin - htick * 2, getWidth() - rmargin, getWidth() - rmargin - htick};
            final int vmargin = 4;
            int[] yp = new int[]{vmargin + htick, vmargin + htick, vmargin};
            if (hiliteUp) {
                g2.drawPolygon(xp, yp, 3);
            } else {
                g2.fillPolygon(xp, yp, 3);
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

    @Override
    public void robotMoveToCenter() {
        if (robot != null) {
            getRootPane().setCursor(MainFrame.transparentCursor);
            robot.mouseMove(MousePressedCoordX, MousePressedCoordY);
        }
    }
}
