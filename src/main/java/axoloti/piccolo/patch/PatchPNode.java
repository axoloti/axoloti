package axoloti.piccolo.patch;

import axoloti.abstractui.PatchView;
import axoloti.piccolo.SwingLayoutNode;
import axoloti.preferences.Theme;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PPaintContext;

public class PatchPNode extends SwingLayoutNode {

    public static final Stroke strokeThin = new BasicStroke(1);
    public static final Stroke strokeThick = new BasicStroke(2);

    public PatchPNode() {
        this(null);
    }

    public PatchPNode(PatchView patchView) {
        this.patchView = patchView;
    }

    protected final PatchView patchView;

    private Color foregroundColor = Theme.getCurrentTheme().Object_Default_Foreground;

    public void setForeground(Color c) {
        this.foregroundColor = c;
    }

    public Color getForeground() {
        return foregroundColor;
    }

    private boolean enabled = true;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Dimension getSize() {
        return getProxyComponent().getSize();
    }

    private String toolTipText;

    public void setToolTipText(String text) {
        this.toolTipText = text;
    }

    public String getToolTipText() {
        return toolTipText;
    }

    private final Stroke stroke = new BasicStroke(1f);
    private boolean drawBorder = false;

    public void setDrawBorder(boolean drawBorder) {
        this.drawBorder = drawBorder;
    }

    private PBounds insetBounds;

    @Override
    protected void paint(PPaintContext paintContext) {
        if (getPaint() != null) {
            final Graphics2D g2 = paintContext.getGraphics();

            if (drawBorder) {
                if (isSelected()) {
                    g2.setPaint(Theme.getCurrentTheme().Object_Border_Selected);
                } else {
                    g2.setPaint(Theme.getCurrentTheme().Object_Border_Unselected);
                }

                g2.fill(getBoundsReference());
                g2.setPaint(getPaint());
                g2.fill(getBounds().inset(1, 1));
            } else {
                g2.setPaint(getPaint());
                g2.fill(getBoundsReference());
            }
        }
    }

    public Boolean isSelected() {
        return ((PatchPCanvas) patchView.getViewportView().getComponent()).isSelected(this);
    }
}
