package axoloti.piccolo.components.displays;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.preferences.Theme;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import org.piccolo2d.util.PPaintContext;

public class PVUComponent extends PDispComponentAbstract {

    private double value;
    private double accumvalue;
    private double peakaccumvalue;
    private static final int w = 6;
    private static final int h = 16;
    private static final Dimension dim = new Dimension(w, h);

    public PVUComponent(IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView);
        value = 0;
        initComponent();
    }

    private void initComponent() {
        setMinimumSize(dim);
        setMaximumSize(dim);
        setPreferredSize(dim);
        setSize(dim);
    }

    double decay = 0.5;

    @Override
    public void setValue(double value) {
        this.value = value / 256.0;
        double valuesq = this.value * this.value;
        accumvalue = (accumvalue * decay) + (valuesq * (1.0 - decay));

        double peakdecay = 0.75;
        peakaccumvalue = (peakaccumvalue * peakdecay) + (valuesq * (1.0 - peakdecay));
        // peak
        if (valuesq > peakaccumvalue) {
            peakaccumvalue = valuesq;
        }
        repaint();
    }

    int valueToPos(double v) {
        double dB = Math.log10(Math.abs(v) + 0.000000001);
        int i = (int) (-dB * 3);
        // 3 pixels per 10 dB
        // with h = 15 this is 50dB range
        if (i > h) {
            i = h;
        }
        return h - i;
    }

    static Color CDarkGreen = Theme.getCurrentTheme().VU_Dark_Green;
    static Color CDarkYellow = Theme.getCurrentTheme().VU_Dark_Yellow;
    static Color CDarkRed = Theme.getCurrentTheme().VU_Dark_Red;

    static Color CBrightGreen = Theme.getCurrentTheme().VU_Bright_Green;
    static Color CBrightYellow = Theme.getCurrentTheme().VU_Bright_Yellow;
    static Color CBrightRed = Theme.getCurrentTheme().VU_Bright_Red;

    static int segmentsRed = 2;
    static int segmentsYellow = 3;
    static int segmentsGreen = h - 5;

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();

        g2.setPaint(CDarkRed);
        g2.fillRect(0, 0, w, segmentsRed);
        g2.setPaint(CDarkYellow);
        g2.fillRect(0, segmentsRed, w, segmentsYellow);
        g2.setPaint(CDarkGreen);
        g2.fillRect(0, segmentsYellow + segmentsRed, w, segmentsGreen);

        int pa = valueToPos(accumvalue);
        if (pa < segmentsGreen) {
            g2.setPaint(CBrightGreen);
            g2.fillRect(1, h - pa, w - 2, pa);
        } else {
            g2.setPaint(CBrightGreen);
            g2.fillRect(1, h - segmentsGreen, w - 2, segmentsGreen);
            if (pa < (segmentsYellow + segmentsGreen)) {
                g2.setPaint(CBrightYellow);
                g2.fillRect(1, h - pa, w - 2, pa - segmentsGreen);
            } else {
                g2.setPaint(CBrightYellow);
                g2.fillRect(1, segmentsRed, w - 2, segmentsYellow);
                g2.setPaint(CBrightRed);
                g2.fillRect(1, h - pa, w - 2, pa - segmentsYellow - segmentsGreen);
            }
        }
        int pp = valueToPos(peakaccumvalue);
        if (pp < segmentsGreen) {
            g2.setPaint(CBrightGreen);
        } else if (pp < (segmentsGreen + segmentsYellow)) {
            g2.setPaint(CBrightYellow);
        } else {
            g2.setPaint(CBrightRed);
        }
        g2.fillRect(1, h - pp, w - 2, 1);
    }

}
