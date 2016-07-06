package axoloti;

import axoloti.object.AxoObjectInstanceAbstract;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JLayer;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

public class ZoomUtils {
    public static void showZoomedPopupMenu(Component component, AxoObjectInstanceAbstract axoObj, JPopupMenu m) {
        PatchGUI patchGUI = ((PatchGUI) axoObj.getPatch());
        double zoom = patchGUI.zoomUI.getScale();
        Point menuLocation = component.getLocationOnScreen();
        SwingUtilities.convertPointFromScreen(menuLocation, patchGUI.Layers);

        menuLocation.x *= zoom;
        menuLocation.y *= zoom;
        
        m.show(patchGUI.Layers, menuLocation.x, (int) Math.round(menuLocation.y + (component.getHeight() * zoom - 1)));
    }
    
    public static Point getToolTipLocation(Component component, MouseEvent event, AxoObjectInstanceAbstract axoObj) {
        PatchGUI patchGUI = ((PatchGUI) axoObj.getPatch());
        double zoom = ((PatchGUI) axoObj.getPatch()).zoomUI.getScale();
        Point p = component.getLocationOnScreen();

        SwingUtilities.convertPointFromScreen(p, patchGUI.Layers);
        
        p.x *= zoom;
        p.y *= zoom;
        
        p = SwingUtilities.convertPoint(patchGUI.Layers, p, component.getParent());
        
        int widthOffset = (int) Math.round(component.getParent().getWidth() / 8 * zoom + 1);
        int heightOffset = (int) Math.round(component.getParent().getHeight() / 2 * zoom + 1);
        
        return new Point(p.x + widthOffset, p.y + heightOffset);
    }
    
    public static void paintObjectLayer(Component component) {
        Container c = SwingUtilities.getAncestorOfClass(JLayer.class, component);
        if (c != null) {
            c.revalidate();
            c.repaint();
        }
    }
}
