package axoloti.piccolo.components.control;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.piccolo.PUtils;
import axoloti.preferences.Theme;
import axoloti.utils.Constants;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.util.ArrayList;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.util.PPaintContext;

public class PButtonComponent extends PCtrlComponentAbstract {

    boolean isHighlighted = false;
    String label;

    public interface ActListener {

        void fire();
    }
    ArrayList<ActListener> actListeners = new ArrayList<>();

    public void addActListener(ActListener al) {
        actListeners.add(al);
    }

    void doPushed() {
        if (isEnabled()) {
            for (ActListener al : actListeners) {
                al.fire();
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
        repaint();
    }

    public PButtonComponent(String label, IAxoObjectInstanceView view) {
        super(view);
        this.label = label;
        initComponent();
    }

    private void initComponent() {
        FontRenderContext frc = new FontRenderContext(null, true, true);
        TextLayout tl = new TextLayout(label, Constants.FONT, frc);
        int width = (int) tl.getBounds().getWidth();
        if (width < 20) {
            width = 20;
        }
        Dimension d = new Dimension(width + 10, 18);
        setSize(d);
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(new Dimension(5000, 18));
    }

    @Override
    public void keyPressed(PInputEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_SPACE) {
            ke.setHandled(true);
        }
    }

    @Override
    public void keyReleased(PInputEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_SPACE) {
            ke.setHandled(true);
        }
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();
        final int radius = 12;
        PUtils.setRenderQualityToHigh(g2);
        if (isFocusOwner()) {
            g2.setStroke(strokeThick);
        } else {
            g2.setStroke(strokeThin);
        }
        if (isHighlighted) {
            g2.setPaint(getForeground());
            g2.fillRoundRect(2, 2, (int) getWidth() - 4, (int) getHeight() - 4, radius, radius);
            g2.setPaint(Theme.getCurrentTheme().Component_Secondary);
            g2.setFont(Constants.FONT);
            g2.drawString(label, 8, (int) getHeight() - 5);
        } else {
            if (isEnabled()) {
                g2.setPaint(Theme.getCurrentTheme().Component_Secondary);
            } else {
                g2.setPaint(Theme.getCurrentTheme().Object_Default_Background);
            }
            g2.fillRoundRect(2, 2, (int) getWidth() - 4, (int) getHeight() - 4, radius, radius);
            g2.setPaint(getForeground());
            g2.drawRoundRect(2, 2, (int) getWidth() - 4, (int) getHeight() - 4, radius, radius);
            g2.setFont(Constants.FONT);
            g2.drawString(label, 8, (int) getHeight() - 5);
        }
        if (isFocusOwner()) {
            g2.setStroke(strokeThin);
        }
        PUtils.setRenderQualityToLow(g2);
    }

    @Override
    public void mouseClicked(PInputEvent e) {
        grabFocus();
        doPushed();
    }

    @Override
    public void mouseExited(PInputEvent e) {
        isHighlighted = false;
        repaint();
    }

    @Override
    public void mousePressed(PInputEvent e) {
        setHighlighted(true);
        e.setHandled(true);
        repaint();
    }

    @Override
    public void mouseReleased(PInputEvent e) {
        setHighlighted(false);
        e.setHandled(true);
        repaint();
    }

    @Override
    public void mouseDragged(PInputEvent e) {
        if (getBoundsReference().contains(e.getCanvasPosition().getX(), e.getCanvasPosition().getY())) {
            setHighlighted(true);
        } else {
            setHighlighted(false);
        }
        e.setHandled(true);
        repaint();
    }

    @Override
    public double getValue() {
        throw new RuntimeException("getValue unsupported");
    }

    @Override
    public void setValue(double value) {
        throw new RuntimeException("setValue unsupported");
    }
}
