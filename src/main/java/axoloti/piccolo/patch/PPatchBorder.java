package axoloti.piccolo.patch;

import axoloti.preferences.Theme;
import java.awt.Graphics2D;
import org.piccolo2d.util.PPaintContext;

public class PPatchBorder extends PatchPNode {

    public PPatchBorder() {
        super();
        setPickable(false);
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        final Graphics2D g2 = paintContext.getGraphics();
        g2.setPaint(Theme.getCurrentTheme().Patch_Border);
        g2.setStroke(strokeThin);
        g2.draw(getBoundsReference());
    }
}
