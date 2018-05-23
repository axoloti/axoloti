package axoloti.piccolo.components.control;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.piccolo.PUtils;
import axoloti.preferences.Theme;
import axoloti.utils.KeyUtils;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.util.PPaintContext;

public class PHRadioComponent extends PCtrlComponentAbstract {

    double value;
    int n;
    int bsize = 12;

    public PHRadioComponent(int value, int n, IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView);
        this.value = 0;//value;
        this.n = n;
        bsize = 12;
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

    int mousePosToVal(int x, int y) {
        int i = x / bsize;
        if (i < 0) {
            return 0;
        }
        if (i > n - 1) {
            return n - 1;
        }
        return i;
    }

    @Override
    protected void mouseDragged(PInputEvent e) {
        if (dragAction) {
            Point localPosition = PUtils.asPoint(e.getPositionRelativeTo(this));
            fireValue(mousePosToVal(localPosition.x, localPosition.y));
        }
    }

    @Override
    protected void mousePressed(PInputEvent e) {
        if (!e.isPopupTrigger()) {
            grabFocus();
            if (e.getButton() == 1) {
                fireEventAdjustmentBegin();
                Point localPosition = PUtils.asPoint(e.getPositionRelativeTo(this));
                fireValue(mousePosToVal(localPosition.x, localPosition.y));
                dragAction = true;
            }
            e.setHandled(true);
        }
    }

    @Override
    protected void mouseReleased(PInputEvent e) {
        if (!e.isPopupTrigger()) {
            fireEventAdjustmentFinished();
            dragAction = false;
            e.setHandled(true);
        }
    }

    @Override
    public void keyPressed(PInputEvent ke) {
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
                fireEventAdjustmentBegin();
                fireValue(v);
                ke.setHandled(true);
                return;
            }
            case KeyEvent.VK_UP:
            case KeyEvent.VK_RIGHT: {
                int v = (int) value + 1;
                if (v >= n) {
                    v = n - 1;
                }
                fireEventAdjustmentBegin();
                fireValue(v);
                ke.setHandled(true);
                return;
            }
            case KeyEvent.VK_HOME: {
                fireEventAdjustmentBegin();
                fireValue(0);
                fireEventAdjustmentFinished();
                ke.setHandled(true);
                return;
            }
            case KeyEvent.VK_END: {
                fireEventAdjustmentBegin();
                fireValue(n - 1);
                fireEventAdjustmentFinished();
                ke.setHandled(true);
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
                    fireEventAdjustmentBegin();
                    fireValue(i);
                    fireEventAdjustmentFinished();
                }
                ke.setHandled(true);
                break;
            default:
                break;
        }
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();
        if (isEnabled()) {
            g2.setColor(Theme.getCurrentTheme().Component_Secondary);
        } else {
            g2.setColor(Theme.getCurrentTheme().Object_Default_Background);
        }
        PUtils.setRenderQualityToHigh(g2);
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
        }
        if (isFocusOwner()) {
            g2.setStroke(strokeThin);
        }
        PUtils.setRenderQualityToLow(g2);
    }

    @Override
    public void setValue(double value) {
        if (this.value != value) {
            this.value = value;
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

    public void setMax(int n) {
        this.n = n;
    }

    @Override
    void keyReleased(PInputEvent ke) {
        if (isEnabled()) {
            switch (ke.getKeyCode()) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_LEFT:
                    fireEventAdjustmentFinished();
                    ke.setHandled(true);
                    break;
                default:
            }
        }
    }
}
