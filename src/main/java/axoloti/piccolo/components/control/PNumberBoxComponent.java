package axoloti.piccolo.components.control;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.datatypes.ValueFrac32;
import axoloti.piccolo.PUtils;
import axoloti.preferences.Theme;
import axoloti.realunits.NativeToReal;
import axoloti.swingui.TransparentCursor;
import axoloti.swingui.components.control.NumberBoxComponent;
import axoloti.utils.Constants;
import axoloti.utils.KeyUtils;
import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.util.PPaintContext;

public class PNumberBoxComponent extends PCtrlComponentAbstract {

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

    int rmargin = 5;
    int htick = 3;

    public void setNative(List<NativeToReal> convs) {
        this.convs = convs;
    }

    public PNumberBoxComponent(double value, double min, double max, double tick, IAxoObjectInstanceView view) {
        this(value, min, max, tick, 50, 16, view);
    }

    public PNumberBoxComponent(double value, double min, double max, double tick, int hsize, int vsize, IAxoObjectInstanceView view) {
        super(view);
        this.value = value;
        this.min = min;
        this.max = max;
        this.tick = tick;
        initComponent(hsize, vsize);
    }

    private void initComponent(int hsize, int vsize) {
        Dimension d = new Dimension(hsize, vsize);
        setPreferredSize(d);
        setMaximumSize(d);
        setMinimumSize(d);
    }

    final int layoutTick = 3;

    @Override
    public void keyboardFocusGained(PInputEvent event) {
        keybBuffer = "";
    }

    @Override
    public void keyboardFocusLost(PInputEvent event) {
        keybBuffer = "";
    }

    @Override
    protected void mouseDragged(PInputEvent e) {
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
                v = value + t * (mousePressedCoordY - PUtils.getYOnScreen(e));
                if (robot == null) {
                    try {
                        robot = new Robot(MouseInfo.getPointerInfo().getDevice());
                    } catch (AWTException ex) {
                        Logger.getLogger(NumberBoxComponent.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                robotMoveToCenter(e);
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
    int mousePressedCoordX = 0;
    int mousePressedCoordY = 0;
    int mousePressedBtn = 0;

    @Override
    protected void mousePressed(PInputEvent e) {
        if (!e.isPopupTrigger()) {
            grabFocus();
            Point localPosition = PUtils.asPoint(e.getPositionRelativeTo(this));

            if (isEnabled() && (localPosition.x >= getWidth() - rmargin - htick * 2)) {
                dragging = false;
                if (localPosition.y > getHeight() / 2) {
                    hiliteDown = true;
                    fireEventAdjustmentBegin();
                    fireValue(value - tick);
                    fireEventAdjustmentFinished();
                } else {
                    hiliteUp = true;
                    fireEventAdjustmentBegin();
                    fireValue(value + tick);
                    fireEventAdjustmentFinished();
                }
            } else {
                dragging = true;
                mousePressedCoordX = PUtils.getXOnScreen(e);
                mousePressedCoordY = PUtils.getYOnScreen(e);
                mousePressedBtn = e.getButton();
                e.pushCursor(TransparentCursor.get());
                fireEventAdjustmentBegin();
            }
            e.setHandled(true);
        }
    }

    @Override
    protected void mouseReleased(PInputEvent e) {
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
            e.setHandled(true);
        }
        e.popCursor();
        robot = null;
    }

    @Override
    public void keyPressed(PInputEvent ke) {
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
                    ke.setHandled(true);
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_LEFT:
                    fireEventAdjustmentBegin();
                    fireValue(getValue() - steps);
                    ke.setHandled(true);
                    break;
                case KeyEvent.VK_PAGE_UP:
                    fireEventAdjustmentBegin();
                    fireValue(getValue() + 5 * steps);
                    ke.setHandled(true);
                    break;
                case KeyEvent.VK_PAGE_DOWN:
                    fireEventAdjustmentBegin();
                    fireValue(getValue() - 5 * steps);
                    ke.setHandled(true);
                    break;
                case KeyEvent.VK_HOME:
                    fireEventAdjustmentBegin();
                    fireValue(getMin());
                    fireEventAdjustmentFinished();
                    ke.setHandled(true);
                    break;
                case KeyEvent.VK_END:
                    fireEventAdjustmentBegin();
                    fireValue(getMax());
                    fireEventAdjustmentFinished();
                    ke.setHandled(true);
                    break;
                case KeyEvent.VK_ENTER:
                    fireEventAdjustmentBegin();
                    try {
                        fireValue(Float.parseFloat(keybBuffer));
                    } catch (java.lang.NumberFormatException ex) {
                    }
                    fireEventAdjustmentFinished();
                    keybBuffer = "";
                    ke.setHandled(true);
                    repaint();
                    break;
                case KeyEvent.VK_BACK_SPACE:
                    if (keybBuffer.length() > 0) {
                        keybBuffer = keybBuffer.substring(0, keybBuffer.length() - 1);
                    }
                    ke.setHandled(true);
                    repaint();
                    break;
                case KeyEvent.VK_ESCAPE:
                    keybBuffer = "";
                    ke.setHandled(true);
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
                    ke.setHandled(true);
                    repaint();
                    break;
                default:
            }
        }
    }
    private static final Stroke strokeThin = new BasicStroke(1);
    private static final Stroke strokeThick = new BasicStroke(2);

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();
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

        g2.fillRect(0, 0, (int) getWidth(), (int) getHeight());
        g2.setPaint(getForeground());
        g2.drawRect(0, 0, (int) getWidth(), (int) getHeight());

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
        PUtils.setRenderQualityToHigh(g2);
        if (keybBuffer.isEmpty()) {
            g2.setFont(Constants.FONT);
            g2.drawString(s, h, getSize().height - v);
        } else {
            g2.setColor(Theme.getCurrentTheme().Error_Text);
            g2.setFont(Constants.FONT);
            g2.drawString(keybBuffer, h, getSize().height - v);
        }
        PUtils.setRenderQualityToLow(g2);

        if (getWidth() < 20) {
            rmargin = -1;
            htick = 1;
        }
        g2.setStroke(strokeThin);
        {
            int[] xp = new int[]{(int) getWidth() - rmargin - htick * 2, (int) getWidth() - rmargin, (int) getWidth() - rmargin - htick};
            final int vmargin = (int) getHeight() - htick - 3;
            int[] yp = new int[]{vmargin, vmargin, vmargin + htick};
            if (hiliteDown) {
                g2.drawPolygon(xp, yp, 3);
            } else {
                g2.fillPolygon(xp, yp, 3);
            }
        }
        {
            int[] xp = new int[]{(int) getWidth() - rmargin - htick * 2, (int) getWidth() - rmargin, (int) getWidth() - rmargin - htick};
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
    void keyReleased(PInputEvent ke) {
        if (isEnabled()) {
            switch (ke.getKeyCode()) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_PAGE_UP:
                case KeyEvent.VK_PAGE_DOWN:
                    fireEventAdjustmentFinished();
                    ke.setHandled(true);
                    break;
                default:
            }
        }
    }

    @Override
    public void robotMoveToCenter(PInputEvent e) {
        robot.mouseMove(mousePressedCoordX, mousePressedCoordY);
    }
}
