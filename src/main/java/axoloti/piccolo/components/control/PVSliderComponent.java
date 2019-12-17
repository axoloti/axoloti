package axoloti.piccolo.components.control;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.preferences.Theme;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.util.PPaintContext;

public class PVSliderComponent extends PCtrlComponentAbstract {

    double value;
    double max;
    double min;
    double tick;

    private static final int height = 128;
    private static final int width = 12;
    private static final Dimension dim = new Dimension(width, height);
    private String keybBuffer = "";

    public PVSliderComponent(double value, double min, double max, double tick, IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView);
        this.max = max;
        this.min = min;
        this.value = value;
        this.tick = tick;
        initComponent();
    }

    private void initComponent() {
        setPreferredSize(dim);
        setMaximumSize(dim);
        setMinimumSize(dim);
    }
    private int px;
    private int py;

    private void updateValue(PInputEvent e) {
        fireValue(min + (1 - globalToLocal(e.getPosition()).getY() / getHeight()) * ((max - min) / tick) * tick);
    }

    @Override
    protected void mouseDragged(PInputEvent e) {
        if (isEnabled()) {
            updateValue(e);
        }
    }

    @Override
    protected void mousePressed(PInputEvent e) {
        if (!e.isPopupTrigger()) {
            grabFocus();
            updateValue(e);
            e.setHandled(true);
            fireEventAdjustmentBegin();
        }
    }

    @Override
    protected void mouseReleased(PInputEvent e) {
        if (!e.isPopupTrigger()) {
            fireEventAdjustmentFinished();
        }
    }

    @Override
    public void keyPressed(PInputEvent ke) {
        double steps = tick;
        if (ke.isShiftDown()) {
            steps = 8 * tick;
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
                fireValue(max);
                fireEventAdjustmentFinished();
                ke.setHandled(true);
                break;
            case KeyEvent.VK_END:
                fireEventAdjustmentBegin();
                fireValue(min);
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

    final int margin = 2;

    int valToPos(double v) {
        return (int) (margin + ((max - v) * (height - 2 * margin)) / (max - min));
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();
        if (isEnabled()) {
            g2.setPaint(Theme.getCurrentTheme().Component_Secondary);
            g2.fillRect(0, 0, (int) getWidth(), height);
            g2.setPaint(getForeground());
            if (isFocusOwner()) {
                g2.setStroke(strokeThick);
            } else {
                g2.setStroke(strokeThin);
            }
            g2.drawRect(0, 0, (int) getWidth(), height);
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
            g2.drawLine(0, p, (int) getWidth(), p);
            //String s = String.format("%5.2f", value);
            //Rectangle2D r = g2.getFontMetrics().getStringBounds(s, g);
            //g2.drawString(s, bwidth+(margin/2)-(int)(0.5 + r.getWidth()/2), getHeight());
        } else {
            g2.setPaint(Theme.getCurrentTheme().Object_Default_Background);
            g2.fillRect(0, 0, (int) getWidth(), height);
            g2.setPaint(getForeground());
            g2.setStroke(strokeThin);
            g2.drawRect(0, 0, (int) getWidth(), height);
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
        getCanvas().setToolTipText("" + value);
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
}
