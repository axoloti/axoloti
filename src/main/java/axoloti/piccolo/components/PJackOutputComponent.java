package axoloti.piccolo.components;

import axoloti.piccolo.patch.PatchPNode;
import axoloti.piccolo.patch.object.outlet.POutletInstanceView;
import axoloti.preferences.Theme;
import java.awt.BasicStroke;
import static java.awt.Component.CENTER_ALIGNMENT;
import static java.awt.Component.RIGHT_ALIGNMENT;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import org.piccolo2d.util.PPaintContext;

public class PJackOutputComponent extends PatchPNode {

    private static final int sz = 10;
    private static final int margin = 2;
    private static final int inset = 1;
    private static final Dimension dim = new Dimension(sz, sz);

    private boolean connected = false;

    public PJackOutputComponent(POutletInstanceView outletInstanceView) {
        super(outletInstanceView.getPatchView());
        initComponent();
    }

    private void initComponent() {
        setMinimumSize(dim);
        setMaximumSize(dim);
        setPreferredSize(dim);
        setSize(dim);
        setAlignmentY(CENTER_ALIGNMENT);
        setAlignmentX(RIGHT_ALIGNMENT);
    }
    private final static Stroke stroke = new BasicStroke(1.5f);

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();
        g2.setStroke(stroke);

        g2.setPaint(Theme.getCurrentTheme().Object_TitleBar_Background);
        if (connected) {
            g2.fillRect(margin, margin + 1, sz - margin - margin, sz - margin - margin);
        }
        g2.drawRect(margin, margin + 1, sz - margin - margin, sz - margin - margin);

        g2.setPaint(getForeground());
        if (connected) {
            g2.fillRect(margin - 1, margin, sz - margin - margin, sz - margin - margin);
        }
        g2.drawRect(margin - 1, margin, sz - margin - margin, sz - margin - margin);
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
