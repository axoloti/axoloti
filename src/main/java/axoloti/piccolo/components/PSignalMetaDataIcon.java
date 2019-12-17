package axoloti.piccolo.components;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.datatypes.SignalMetaData;
import axoloti.piccolo.patch.PatchPNode;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import org.piccolo2d.util.PPaintContext;

public class PSignalMetaDataIcon extends PatchPNode {

    private final SignalMetaData smd;

    public PSignalMetaDataIcon(SignalMetaData smd, IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView.getPatchView());
        this.smd = smd;
        initComponent();
    }

    private void initComponent() {
        Dimension d = new Dimension(12, 14);
        setMinimumSize(d);
        setMaximumSize(d);
        setPreferredSize(d);
        setName("icon");
    }
    private final int x1 = 2;
    private final int x2 = 5;
    private final int x2_5 = 7;
    private final int x3 = 9;
    private final int x4 = 12;
    private final int y1 = 12;
    private final int y2 = 2;
    private static final Stroke stroke = new BasicStroke(1.0f);

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();
        g2.setStroke(stroke);
        switch (smd) {
            case rising:
                g2.setColor(getForeground());
                g2.drawLine(x1, y1, x2_5, y1); // _
                g2.drawLine(x2_5, y1, x2_5, y2); // /
                g2.drawLine(x2_5, y2, x4, y2); // -
                break;
            case falling:
                g2.setColor(getForeground());
                g2.drawLine(x1, y2, x2_5, y2); // _
                g2.drawLine(x2_5, y1, x2_5, y2); // /
                g2.drawLine(x2_5, y1, x4, y1); // -
                break;
            case risingfalling:
                g2.setColor(getForeground());
                g2.drawLine(x1, y1, x2, y1); // _
                g2.drawLine(x2, y2, x3, y2); // -
                g2.drawLine(x3, y1, x4, y1); // _
                g2.drawLine(x2, y1, x2, y2); // /
                g2.drawLine(x3, y2, x3, y1); // \
                break;
            case pulse:
                g2.setColor(getForeground());
                g2.drawLine(x1, y1, x4, y1); // __
                g2.drawLine(x2_5, y1, x2_5, y2); // |
                break;
            case bipolar:
                g2.setColor(getForeground());
                g2.drawLine(6, 2, 6, 8); // verti
                g2.drawLine(3, 5, 9, 5); // hori

                g2.drawLine(3, 10, 9, 10); // hori
/*
                 g2.drawLine(6, 3, 6, 7); // verti
                 g2.drawLine(4, 5, 8, 5); // hori
                 g2.drawLine(4, 9, 8, 9); // hori
                 */
                break;
            case positive:
                g2.setColor(getForeground());
                g2.drawLine(6, 4, 6, 10); // verti
                g2.drawLine(3, 7, 9, 7); // hori
                break;
            default:
                break;
        }
    }
}
