package axoloti.piccolo.components.control;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.piccolo.PUtils;
import axoloti.preferences.Theme;
import axoloti.utils.KeyUtils;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.util.PPaintContext;

/**
 *
 * @author nicolas
 */
public class PCheckbox4StatesComponent extends PCtrlComponentAbstract {

    private double value;
    private final int n;
    private final int bsize = 12;
    private int selIndex = -1;

    public PCheckbox4StatesComponent(int value, int n, IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView);
        this.value = 0;//value;
        this.n = n;
        initComponent();
    }

    private void initComponent() {
        Dimension d = new Dimension(bsize * n + 2, bsize + 2);
        setMinimumSize(d);
        setMaximumSize(d);
        setPreferredSize(d);
        setSize(d);
    }

    private boolean dragAction = false;
    private int dragValue;

    @Override
    protected void mouseDragged(PInputEvent e) {
        if (dragAction) {
            Point localPosition = PUtils.asPoint(e.getPositionRelativeTo(this));
            int i = localPosition.x / bsize;
            if ((i >= 0) && (i < n)) {
                selIndex = i;
                setFieldValue(i, dragValue);
            }
        }
    }

    private void setFieldValue(int position, int val) {
        int mask = 3 << (position * 2);
        int v = (int) value;
        v = (v & ~mask) + (val << (position * 2));
        fireValue((double) v);
    }

    private int getFieldValue(int position) {
        return (((int) value) >> (position * 2)) & 3;
    }

    @Override
    protected void mousePressed(PInputEvent e) {
        if (!e.isPopupTrigger()) {
            grabFocus();
            if (e.getButton() == 1) {
                Point localPosition = PUtils.asPoint(e.getPositionRelativeTo(this));
                int i = localPosition.x / bsize;
                if ((i >= 0) && (i < n)) {
                    fireEventAdjustmentBegin();
                    if (e.isShiftDown()) {
                        dragValue = getFieldValue(i);
                    } else {
                        dragValue = (getFieldValue(i) + 1) & 3;
                    }
                    setFieldValue(i, dragValue);
                    selIndex = i;
                    dragAction = true;
                }
                e.setHandled(true);
            }
        }
    }

    @Override
    protected void mouseReleased(PInputEvent e) {
        if (!e.isPopupTrigger()) {
            fireEventAdjustmentFinished();
            e.setHandled(true);
        }
        dragAction = false;
    }

    @Override
    public void keyPressed(PInputEvent ke) {
        if (KeyUtils.isIgnoreModifierDown(ke)) {
            return;
        }

        switch (ke.getKeyCode()) {
            case KeyEvent.VK_LEFT: {
                selIndex -= 1;
                if (selIndex < 0) {
                    selIndex = n - 1;
                }
                repaint();
                ke.setHandled(true);
                return;
            }
            case KeyEvent.VK_RIGHT: {
                selIndex += 1;
                if (selIndex >= n) {
                    selIndex = 0;
                }
                repaint();
                ke.setHandled(true);
                return;
            }
            case KeyEvent.VK_UP: {
                int v = getFieldValue(selIndex) + 1;
                if (v > 3) {
                    v = 3;
                }
                fireEventAdjustmentBegin();
                setFieldValue(selIndex, v);
                fireEventAdjustmentFinished();
                ke.setHandled(true);
                return;
            }
            case KeyEvent.VK_DOWN: {
                int v = getFieldValue(selIndex) - 1;
                if (v < 0) {
                    v = 0;
                }
                fireEventAdjustmentBegin();
                setFieldValue(selIndex, v);
                fireEventAdjustmentFinished();
                ke.setHandled(true);
                return;
            }
            case KeyEvent.VK_PAGE_UP: {
                fireEventAdjustmentBegin();
                setFieldValue(selIndex, 3);
                fireEventAdjustmentFinished();
                ke.setHandled(true);
                return;
            }
            case KeyEvent.VK_PAGE_DOWN: {
                fireEventAdjustmentBegin();
                setFieldValue(selIndex, 0);
                fireEventAdjustmentFinished();
                ke.setHandled(true);
                return;
            }
        }

        switch (ke.getKeyChar()) {
            case '0':
                fireEventAdjustmentBegin();
                setFieldValue(selIndex, 0);
                fireEventAdjustmentFinished();
                ke.setHandled(true);
                break;
            case '1':
                fireEventAdjustmentBegin();
                setFieldValue(selIndex, 1);
                fireEventAdjustmentFinished();
                ke.setHandled(true);
                break;
            case '2':
                fireEventAdjustmentBegin();
                setFieldValue(selIndex, 2);
                fireEventAdjustmentFinished();
                ke.setHandled(true);
                break;
            case '3':
                fireEventAdjustmentBegin();
                setFieldValue(selIndex, 3);
                fireEventAdjustmentFinished();
                ke.setHandled(true);
                break;
            case ' ':
                fireEventAdjustmentBegin();
                setFieldValue(selIndex, (getFieldValue(selIndex) + 1) & 3);
                fireEventAdjustmentFinished();
                ke.setHandled(true);
                break;
            default:
                break;
        }
    }

    private static final Stroke strokeThin = new BasicStroke(1);
    private static final Stroke strokeThick = new BasicStroke(2);

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();
        if (isEnabled()) {
            g2.setColor(Theme.getCurrentTheme().Component_Secondary);
        } else {
            g2.setColor(Theme.getCurrentTheme().Object_Default_Background);
        }
        g2.fillRect(0, 0, bsize * n, bsize + 1);
        g2.setPaint(getForeground());

        if (isFocusOwner()) {
            g2.setStroke(strokeThick);
            g2.drawRect(0, 0, bsize * n + 0, bsize + 1);
            g2.drawRect(selIndex * bsize, 0, bsize, bsize + 1);
        } else {
            g2.drawRect(0, 0, bsize * n, bsize + 1);
        }

        g2.setStroke(strokeThin);
        for (int i = 1; i < n; i++) {
            g2.drawLine(bsize * i, 0, bsize * i, bsize + 1);
        }

        if (isEnabled()) {
            int v = (int) value;
            int inset = 2;
            for (int i = 0; i < n; i++) {
                switch (v & 3) {
                    case 0:
                        g2.setColor(Theme.getCurrentTheme().Component_Secondary);
                        break;
                    case 1:
                        g2.setColor(Theme.getCurrentTheme().Component_Mid_Dark);
                        break;
                    case 2:
                        g2.setColor(Theme.getCurrentTheme().Component_Mid_Light);
                        break;
                    case 3:
                        g2.setColor(Theme.getCurrentTheme().Component_Primary);
                        break;
                    default:
                        // impossible
                        break;
                }
                g2.fillRect(i * bsize + inset, inset, bsize - inset - 1, bsize - inset);
                v >>= 2;
            }
        }
    }

    @Override
    public void setValue(double value) {
        if (this.value != value) {
            this.value = value;
            repaint();
        }
    }

    public void fireValue(double value) {
        setValue(value);
        fireEvent();
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    void keyReleased(PInputEvent ke) {
    }
}
