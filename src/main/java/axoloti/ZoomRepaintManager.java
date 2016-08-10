package axoloti;

import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

public class ZoomRepaintManager extends RepaintManager {

    private ZoomUI zoomUI;

    ZoomRepaintManager(ZoomUI zoomUI) {
        this.zoomUI = zoomUI;
    }

    @Override
    public void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
        JLayer layer = ZoomUtils.getAncestorLayer(c);
        if (layer != null) {
            Rectangle bounds = SwingUtilities.convertRectangle(c, new Rectangle(x, y, w, h), layer);
            zoomUI.scale(bounds);
            super.addDirtyRegion(layer, bounds.x, bounds.y, bounds.width, bounds.height);
        }
        super.addDirtyRegion(c, x, y, w, h);
    }
}
