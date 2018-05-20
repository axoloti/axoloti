package axoloti.piccolo.components.displays;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.piccolo.PUtils;
import axoloti.piccolo.patch.PatchPNode;
import axoloti.utils.Constants;
import java.awt.Dimension;
import java.awt.Graphics2D;
import org.piccolo2d.util.PPaintContext;

public class PVValueLabelsComponent extends PatchPNode {

    private final double max;
    private final double min;
    private final double tick;

    int height = 128;
    int width = 25;

    public PVValueLabelsComponent(double min, double max, double tick,
            IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView.getPatchView());
        this.max = max;
        this.min = min;
        this.tick = tick;
        initComponent();
    }

    private void initComponent() {
        Dimension d = new Dimension(width, height);
        setPreferredSize(d);
        setMaximumSize(d);
        setMinimumSize(d);
    }

    final int margin = 0;

    int valToPos(double v) {
        return (int) (margin + ((max - v) * (height - 2 * margin)) / (max - min));
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();
        g2.setPaint(getForeground());
        int inset = 3;
        for (double v = min + tick; v < max; v += tick) {
            int y = valToPos(v);
            g2.drawLine(width - inset, y, width, y);
            String s;
            if (Math.rint(v) == v) {
                s = String.format("%4.0f", v);
            } else {
                s = String.format("%4.1f", v);
            }
            g2.setFont(Constants.FONT);
            PUtils.setRenderQualityToHigh(g2);
            g2.drawString(s, 0, y + 4);
            PUtils.setRenderQualityToLow(g2);
        }
    }
}
