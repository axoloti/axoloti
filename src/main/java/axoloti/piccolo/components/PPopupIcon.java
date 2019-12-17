package axoloti.piccolo.components;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.piccolo.patch.PatchPNode;
import axoloti.preferences.Theme;
import java.awt.Dimension;
import java.awt.Graphics2D;
import org.piccolo2d.util.PPaintContext;

public class PPopupIcon extends PatchPNode {

    private final Dimension minsize = new Dimension(10, 12);
    private final Dimension maxsize = new Dimension(10, 12);

    public PPopupIcon(IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView.getPatchView());
        initComponent();
    }

    private void initComponent() {
        setMinimumSize(minsize);
        setPreferredSize(maxsize);
        setMaximumSize(maxsize);
        setSize(minsize);
        setPickable(true);
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();
        g2.setColor(Theme.getCurrentTheme().Component_Primary);
        final int rmargin = 3;
        final int htick = 3;
        int[] xp = new int[]{(int) (getBoundsReference().width - rmargin - htick * 2),
            (int) (getBoundsReference().width - rmargin),
            (int) (getBoundsReference().width - rmargin - htick)};
        final int vmargin = 3;
        int[] yp = new int[]{vmargin, vmargin, vmargin + htick * 2};
        if (isEnabled()) {
            g2.fillPolygon(xp, yp, 3);
        } else {
            g2.drawPolygon(xp, yp, 3);
        }
    }
}
