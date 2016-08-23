package components.piccolo.displays;

import axoloti.Theme;
import axoloti.objectviews.IAxoObjectInstanceView;
import java.awt.Dimension;
import java.awt.Graphics2D;
import org.piccolo2d.util.PPaintContext;

public class PVLineComponentDB extends PDispComponentAbstract {

    private double value;
    private double max;
    private double min;

    int height = 128;
    int width = 1;

    public PVLineComponentDB(double value, double min, double max, IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView);
        this.max = max;
        this.min = min;
        this.value = value;
        Dimension d = new Dimension(width, height);
        setPreferredSize(d);
        setMaximumSize(d);
        setMinimumSize(d);
    }
    int px;

    final int margin = 0;

    int ValToPos(double v) {
        double dB = -1000;
        if (v != 0) {
            dB = 20 * Math.log10(Math.abs(v) / 64.0);
        }
        if (dB > max) {
            dB = max;
        }
        if (dB < min) {
            dB = min;
        }
        return (int) (margin + ((max - dB) * (height - 2 * margin)) / (max - min));
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();
        g2.setPaint(Theme.getCurrentTheme().Component_Secondary);
        g2.fillRect(0, 0, (int) getBoundsReference().width, height);
        int p = ValToPos(value);
        int p1 = ValToPos(0);
        g2.setPaint(Theme.getCurrentTheme().Component_Mid);
        g2.drawLine(0, p, 0, p1);
    }

    @Override
    public void setValue(double value) {
        this.value = value;
        if (this.value != value) {
            this.value = value;
            repaint();
        }
    }

    public void setMinimum(double min) {
        this.min = min;
    }

    public double getMinimum() {
        return min;
    }

    public void setMaximum(double max) {
        this.max = max;
    }

    public double getMaximum() {
        return max;
    }
}
