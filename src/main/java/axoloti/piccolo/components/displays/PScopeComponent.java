package axoloti.piccolo.components.displays;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.piccolo.PUtils;
import axoloti.preferences.Theme;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import org.piccolo2d.util.PPaintContext;

public class PScopeComponent extends PDispComponentAbstract {

    private final int length = 64;
    private final int vsize = 64;
    private final int[] value = new int[length];
    private final int[] xvalue = new int[length];
    private int index = 0;
    private final double max;
    private final double min;

    public PScopeComponent(double min, double max, IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView);
        this.max = max;
        this.min = min;
        for (int i = 0; i < length; i++) {
            xvalue[i] = i + 1;
        }
        initComponent();
    }

    private void initComponent() {
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
        g2.setPaint(getForeground());
        g2.drawRect(0, 0, length + 2, vsize + 2);
        g2.setStroke(strokeThin);
        PUtils.setRenderQualityToHigh(g2);
        if (index > 1) {
            g2.drawPolyline(xvalue, value, index - 1);
        }
        g2.setColor(Theme.getCurrentTheme().Component_Mid);
        if (index < length - 2) {
            g2.drawPolyline(java.util.Arrays.copyOfRange(xvalue, index, length - 1),
                    java.util.Arrays.copyOfRange(value, index, length - 1), length - index - 1);
        }
        int v = (int) project(0);
        PUtils.setRenderQualityToLow(g2);
        g2.drawLine(0, v, length, v);
    }

    @Override
    public void setValue(double value) {
        if (value < min) {
            value = min;
        }
        if (value > max) {
            value = max;
        }
        this.value[index++] = (int) project(value);
        if (index >= length) {
            index = 0;
        }
        repaint();
    }

    double project(double value) {
        return (1 + (vsize * (max - value)) / ((max - min)));
    }

    public double getMinimum() {
        return min;
    }

    public double getMaximum() {
        return max;
    }
}
