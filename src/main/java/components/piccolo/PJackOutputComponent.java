package components.piccolo;

import axoloti.Theme;
import axoloti.piccolo.PatchPNode;
import axoloti.piccolo.outlets.POutletInstanceView;
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

    final POutletInstanceView outletInstanceView;

    public PJackOutputComponent(POutletInstanceView outletInstanceView) {
        super(outletInstanceView.getPatchView());
        setMinimumSize(dim);
        setMaximumSize(dim);
        setPreferredSize(dim);
        setSize(dim);
        setAlignmentY(CENTER_ALIGNMENT);
        setAlignmentX(RIGHT_ALIGNMENT);
        this.outletInstanceView = outletInstanceView;
    }
    private final static Stroke stroke = new BasicStroke(1.5f);

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();
        g2.setStroke(stroke);

        g2.setPaint(Theme.getCurrentTheme().Object_TitleBar_Background);
        if (outletInstanceView.getModel().isConnected()) {
            g2.fillRect(margin, margin + 1, sz - margin - margin, sz - margin - margin);
        }
        g2.drawRect(margin, margin + 1, sz - margin - margin, sz - margin - margin);

        g2.setPaint(getForeground());
        if (outletInstanceView.getModel().isConnected()) {
            g2.fillRect(margin - 1, margin, sz - margin - margin, sz - margin - margin);
        }
        g2.drawRect(margin - 1, margin, sz - margin - margin, sz - margin - margin);
    }
}
