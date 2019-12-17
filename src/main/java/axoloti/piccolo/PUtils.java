package axoloti.piccolo;

import axoloti.utils.Constants;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import org.piccolo2d.PNode;
import org.piccolo2d.event.PInputEvent;

public class PUtils {

    private PUtils() {
    }

    public static Point asPoint(Point2D p) {
        return new Point((int) Math.round(p.getX()), (int) Math.round(p.getY()));
    }

    private static Point2D getRawPopupLocation(PNode node) {
        Point2D location = node.getBoundsReference().getOrigin();
        location.setLocation(location.getX(),
                location.getY() + node.getBoundsReference().getHeight());
        return location;
    }

    private static void rawToCanvas(PInputEvent e, Point2D popupLocation) {
        e.getPath().getPathTransformTo(e.getPickedNode()).transform(popupLocation, popupLocation);
    }

    public static Point getPopupLocation(PNode node, PInputEvent e) {
        Point2D location = getRawPopupLocation(node);
        location = node.localToParent(location);
        location = node.localToParent(location);
        rawToCanvas(e, location);
        return asPoint(location);
    }

    public static Point getPopupLocation(PInputEvent e) {
        Point2D location = getRawPopupLocation(e.getPickedNode());
        rawToCanvas(e, location);
        return asPoint(location);
    }

    public static void setRenderQualityToLow(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
    }

    public static void setRenderQualityToHigh(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
    }

    public static void printNodeStats(PNode node) {
        int visibleNodes = 0;
        Map<String, Integer> typeCount = new HashMap<>();
        for (Object n : node.getAllNodes()) {
            if (((PNode) n).getVisible()) {
                visibleNodes++;
            }
            String key = n.getClass().toString();
            if (typeCount.containsKey(key)) {
                typeCount.put(key, typeCount.get(key) + 1);
            } else {
                typeCount.put(key, 1);
            }
        }

        System.out.println("node count: " + node.getAllNodes().size());
        System.out.println("visible node count: " + visibleNodes);
        for (String key : typeCount.keySet()) {
            System.out.println(key + ": " + typeCount.get(key));
        }
    }

    public static boolean viewScaleWithinLimits(double currentViewScale, double attemptedScaleFactor) {
        double newViewScale = currentViewScale * attemptedScaleFactor;
        return newViewScale > Constants.PICCOLO_VIEW_SCALE_LOWER_BOUND &&
               newViewScale < Constants.PICCOLO_VIEW_SCALE_UPPER_BOUND;
    }

    public static int getXOnScreen(PInputEvent e) {
        return ((MouseEvent) e.getSourceSwingEvent()).getXOnScreen();
    }

    public static int getYOnScreen(PInputEvent e) {
        return ((MouseEvent) e.getSourceSwingEvent()).getYOnScreen();
    }
}
