package axoloti.piccolo.components.control;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.piccolo.PUtils;
import axoloti.preferences.Theme;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.util.PPaintContext;

public class PPulseButtonComponent extends PCtrlComponentAbstract {

    private double value;
    private final static int bsize = 15;
    private final static Dimension dim = new Dimension(bsize, bsize);

    public PPulseButtonComponent(IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView);
        this.value = 0;
        initComponent();
    }

    private void initComponent() {
        setMinimumSize(dim);
        setMaximumSize(dim);
        setPreferredSize(dim);
        setSize(dim);
    }

    @Override
    protected void mouseDragged(PInputEvent e) {
    }

    @Override
    protected void mousePressed(PInputEvent e) {
        grabFocus();
        if (e.getButton() == 1) {
            fireValue(1.0);
        }
    }

    @Override
    protected void mouseReleased(PInputEvent e) {
        if (e.getButton() == 1) {
            if (!e.isShiftDown()) {
                fireValue(0.0);
            }
        }
    }

    @Override
    public void keyPressed(PInputEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_SPACE) {
            fireValue(1.0);
            ke.setHandled(true);
        }
    }

    @Override
    void keyReleased(PInputEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_SPACE) {
            fireValue(1.0);
            ke.setHandled(true);
        }
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();
        if (isFocusOwner()) {
            g2.setStroke(strokeThick);
        } else {
            g2.setStroke(strokeThin);
        }
        PUtils.setRenderQualityToHigh(g2);
        int v = (int) value;
        if (v > 0.0) {
            g2.setPaint(getForeground());
            g2.drawOval(2, 2, bsize - 5, bsize - 5);
            g2.fillOval(2, 2, bsize - 5, bsize - 5);
        } else {
            g2.setColor(Theme.getCurrentTheme().Component_Secondary);
            g2.fillOval(2, 2, bsize - 5, bsize - 5);
            g2.setPaint(getForeground());
            g2.drawOval(2, 2, bsize - 5, bsize - 5);
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
}
