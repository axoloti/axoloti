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

import axoloti.datatypes.ValueFrac32;
import axoloti.preferences.Preferences;
import axoloti.preferences.Theme;
import axoloti.realunits.NativeToReal;
import axoloti.swingui.TransparentCursor;
import axoloti.utils.Constants;
import axoloti.utils.KeyUtils;
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
import java.util.List;

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
    private List<NativeToReal> convs;
    private String keybBuffer = "";

    private boolean hiliteUp = false;
    private boolean hiliteDown = false;
    private boolean dragging = false;

    private Robot robot;

    private int rmargin = 5;
    private int htick = 3;

    public void setNative(List<NativeToReal> convs) {
        this.convs = convs;
    }

    public NumberBoxComponent(double value, double min, double max, double tick) {
        this(value, min, max, tick, 50, 16);
    }

    public NumberBoxComponent(double value, double min, double max, double tick, int hsize, int vsize) {
        super();
        this.value = value;
        this.min = min;
        this.max = max;
        this.tick = tick;

        initComponent(hsize, vsize);
    }

    private void initComponent(int hsize, int vsize) {
        setInheritsPopupMenu(true);
        Dimension d = new Dimension(hsize, vsize);
        setSize(d);
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
        setupTransferHandler();
    }

    final int layoutTick = 3;

    @Override
    protected void mouseDragged(MouseEvent e) {
        if (isEnabled() && dragging) {
            double v;
            if ((mousePressedBtn == MouseEvent.BUTTON1)) {
                double t = tick;
                t *= 0.1;
                if (e.isShiftDown()) {
                    t *= 0.1;
                }
                if (KeyUtils.isControlOrCommandDown(e)) {
                    t *= 0.1;
                }
                v = value + t * (mousePressedCoordY - e.getYOnScreen());
                this.robotMoveToCenter();
                if (robot == null) {
                    mousePressedCoordY = e.getYOnScreen();
                }
                if (v > max) {
                    v = max;
                }
                if (v < min) {
                    v = min;
                }
                fireValue(v);
            }
        }
    }

    private int mousePressedCoordX = 0;
    private int mousePressedCoordY = 0;
    private int mousePressedBtn = 0;

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
                    fireValue(value - tick);
                    fireEvent();
                    fireEventAdjustmentFinished();
                } else {
                    hiliteUp = true;
                    fireEventAdjustmentBegin();
                    fireValue(value + tick);
                    fireEvent();
                    fireEventAdjustmentFinished();
                }
            } else {
                dragging = true;
                mousePressedCoordX = e.getXOnScreen();
                mousePressedCoordY = e.getYOnScreen();
                mousePressedBtn = e.getButton();
                if (!Preferences.getPreferences().getMouseDoNotRecenterWhenAdjustingControls()) {
                    getRootPane().setCursor(TransparentCursor.get());
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
                steps *= 0.1; // mini steps!
                if (KeyUtils.isControlOrCommandDown(ke)) {
                    steps *= 0.1; // micro steps!
                }
            } else if (KeyUtils.isControlOrCommandDown(ke)) {
                steps *= 10.0; //accelerate!
            }
            switch (ke.getKeyCode()) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_RIGHT:
                    fireEventAdjustmentBegin();
                    fireValue(getValue() + steps);
                    fireEvent();
                    ke.consume();
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_LEFT:
                    fireEventAdjustmentBegin();
                    fireValue(getValue() - steps);
                    fireEvent();
                    ke.consume();
                    break;
                case KeyEvent.VK_PAGE_UP:
                    fireEventAdjustmentBegin();
                    fireValue(getValue() + 5 * steps);
                    fireEvent();
                    ke.consume();
                    break;
                case KeyEvent.VK_PAGE_DOWN:
                    fireEventAdjustmentBegin();
                    fireValue(getValue() - 5 * steps);
                    fireEvent();
                    ke.consume();
                    break;
                case KeyEvent.VK_HOME:
                    fireEventAdjustmentBegin();
                    fireValue(getMin());
                    fireEvent();
                    fireEventAdjustmentFinished();
                    ke.consume();
                    break;
                case KeyEvent.VK_END:
                    fireEventAdjustmentBegin();
                    fireValue(getMax());
                    fireEvent();
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
        g2.setPaint(getBackground());
        g2.drawRect(1, 1, getWidth() - 2, getHeight() - 2);
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
            StringBuilder sb = new StringBuilder();
            sb.append("<html>");
            for (NativeToReal c : convs) {
                sb.append(c.convertToReal(new ValueFrac32(value)));
                sb.append("<br>");
            }
            this.setToolTipText(sb.toString());
        }
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

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
        repaint();
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
        repaint();
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
            getRootPane().setCursor(TransparentCursor.get());
            robot.mouseMove(mousePressedCoordX, mousePressedCoordY);
        }
    }
}
