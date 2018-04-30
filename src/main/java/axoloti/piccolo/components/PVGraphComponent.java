package axoloti.piccolo.components;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.piccolo.PUtils;
import axoloti.piccolo.patch.PatchPNode;
import axoloti.preferences.Theme;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import org.piccolo2d.util.PPaintContext;

public class PVGraphComponent extends PatchPNode {

    private final int length;
    private final int vsize;
    private final int[] ypoints;
    private final int[] xpoints;
    private final int index = 0;
    private final double max;
    private final double min;
    private final int imax;
    private final int imin;
    private int y0;

    public PVGraphComponent(int length, int vsize, double min, double max, IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView.getPatchView());
        this.length = length;
        this.vsize = vsize;
        this.max = max;
        this.min = min;
        this.imax = (int) max;
        this.imin = (int) min;
        this.xpoints = new int[length];
        this.ypoints = new int[length];
        for (int i = 0; i < length; i++) {
            xpoints[i] = i + 1;
            ypoints[i] = vsize;
        }
        initComponent();
    }

    private void initComponent() {
        y0 = valToPos(0);
        Dimension d = new Dimension(length + 2, vsize + 2);
        setMinimumSize(d);
        setMaximumSize(d);
        setPreferredSize(d);
    }
    private static final Stroke strokeThin = new BasicStroke(0.75f);
    private static final Stroke strokeThick = new BasicStroke(1.f);

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();
        g2.setStroke(strokeThick);
        g2.setColor(Theme.getCurrentTheme().Component_Secondary);
        g2.fillRect(0, 0, length + 2, vsize + 2);
        g2.setPaint(Theme.getCurrentTheme().Patch_Unlocked_Background);
        g2.drawLine(0, y0, length, y0);
        g2.setPaint(getForeground());
        g2.drawRect(0, 0, length + 2, vsize + 2);
        g2.setStroke(strokeThin);
        PUtils.setRenderQualityToHigh(g2);
        g2.drawPolyline(xpoints, ypoints, length);
        PUtils.setRenderQualityToLow(g2);
    }

    public int valToPos(int x) {
        if (x < imin) {
            x = imin;
        }
        if (x > imax) {
            x = imax;
        }
        return (int) Math.round((double) ((max - x) * vsize) / (max - min));
    }

    public void setValue(int value[]) {
        for (int i = 0; i < length; i++) {
            this.ypoints[i] = valToPos(value[i]);
        }
        repaint();
    }

    public double getMinimum() {
        return min;
    }

    public double getMaximum() {
        return max;
    }
}
