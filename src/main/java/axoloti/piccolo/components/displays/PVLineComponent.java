package axoloti.piccolo.components.displays;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.preferences.Theme;
import java.awt.Dimension;
import java.awt.Graphics2D;
import org.piccolo2d.util.PPaintContext;

public class PVLineComponent extends PDispComponentAbstract {

    private double value;
    private double max;
    private double min;

    int height = 128;
    int width = 1;

    public PVLineComponent(double value, double min, double max, IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView);
        this.max = max;
        this.min = min;
        this.value = value;
        initComponent();
    }

    private void initComponent() {
        Dimension d = new Dimension(width, height);
        setPreferredSize(d);
        setMaximumSize(d);
        setMinimumSize(d);
    }
    int px;

    final int margin = 0;

    int valToPos(double v) {
        return (int) (margin + ((max - v) * (height - 2 * margin)) / (max - min));
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();
        g2.setPaint(Theme.getCurrentTheme().Component_Secondary);
        g2.fillRect(0, 0, (int) getBoundsReference().width, height);
        int p = valToPos(value);
        int p1 = valToPos(0);
        g2.setPaint(Theme.getCurrentTheme().Component_Mid);
        g2.drawLine(0, p, 0, p1);
    }

    @Override
    public void setValue(double value) {
        if (value > max) {
            value = max;
        }
        if (value < min) {
            value = min;
        }
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
