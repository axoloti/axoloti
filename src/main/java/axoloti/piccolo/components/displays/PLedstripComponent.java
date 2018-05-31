package axoloti.piccolo.components.displays;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.preferences.Theme;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import org.piccolo2d.util.PPaintContext;

public class PLedstripComponent extends PDispComponentAbstract {

    private double value;
    private final int n;
    private static final int bsize = 12;

    public PLedstripComponent(int value, int n, IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView);
        this.value = 0;
        this.n = n;
        initComponent();
    }

    private void initComponent() {
        Dimension d = new Dimension(bsize * n + 2, bsize + 2);
        setMinimumSize(d);
        setPreferredSize(d);
        setMaximumSize(d);
        setSize(d);
    }

    final Color c_off = Theme.getCurrentTheme().Led_Strip_Off;
    final Color c_on = Theme.getCurrentTheme().Led_Strip_On;

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();
        g2.setColor(Theme.getCurrentTheme().Object_Default_Background);
        g2.fillRect(0, 0, bsize * n + 1, bsize + 1);
//        g2.setPaint(getForeground());
//        g2.drawRect(0, 0, bsize * n + 1, bsize + 1);
//        for (int i = 1; i < n; i++) {
//            g2.drawLine(bsize * i, 0, bsize * i, bsize + 1);
//        }

        int v = (int) value;
        int inset = 3;
        for (int i = 0; i < n; i++) {
            if ((v & 1) != 0) {
                g2.setColor(c_on);
            } else {
                g2.setColor(c_off);
            }
            g2.fillRect(i * bsize + inset, inset, bsize - inset - 1, bsize - inset);
            v >>= 1;
        }
    }

    @Override
    public void setValue(double value) {
        if (this.value != value) {
            this.value = value;
            repaint();
        }
    }
}
