package axoloti.piccolo.patch.net;

import axoloti.abstractui.IIoletInstanceView;
import axoloti.patch.net.Net;
import axoloti.piccolo.PUtils;
import static axoloti.piccolo.PUtils.asPoint;
import axoloti.piccolo.patch.PatchViewPiccolo;
import axoloti.preferences.Theme;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import org.piccolo2d.util.PPaintContext;

public class PNetDragging extends PNetView {

    public PNetDragging(Net net, PatchViewPiccolo patchView) {
        super(net, patchView);
    }

    Point p0;

    public void setDragPoint(Point p0) {
        this.p0 = p0;

        updateBounds();
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();
        float shadowOffset = 0.5f;
        Color c;
        if (getDModel().isValidNet()) {
            if (selected) {
                g2.setStroke(strokeValidSelected);
            } else {
                g2.setStroke(strokeValidDeselected);
            }

            c = getDModel().getDataType().getColor();
        } else {
            if (selected) {
                g2.setStroke(strokeBrokenSelected);
            } else {
                g2.setStroke(strokeBrokenDeselected);
            }

            if (getDModel().getDataType() != null) {
                c = getDModel().getDataType().getColor();
            } else {
                c = Theme.getCurrentTheme().Cable_Shadow;
            }
        }
        if (p0 != null) {
            if (boundsChangedSincePaint) {
                Point2D from = asPoint(globalToLocal(p0));
                for (IIoletInstanceView i : getInletViews()) {
                    Point2D to = asPoint(globalToLocal(i.getJackLocInCanvas()));
                    setCurveShape(getIoletCurve(i), from, to);
                }
                for (IIoletInstanceView i : getOutletViews()) {
                    Point2D to = asPoint(globalToLocal(i.getJackLocInCanvas()));
                    setCurveShape(getIoletCurve(i), from, to);
                }
                boundsChangedSincePaint = false;
            }

            PUtils.setRenderQualityToHigh(g2);
            for (IIoletInstanceView i : getInletViews()) {
                g2.setColor(c);
                g2.draw(ioletCurves.get(i));
            }
            for (IIoletInstanceView i : getOutletViews()) {
                g2.setColor(c);
                g2.draw(ioletCurves.get(i));
            }
            PUtils.setRenderQualityToLow(g2);
        }
    }

    @Override
    public void updateBounds() {
        int min_y = Integer.MAX_VALUE;
        int min_x = Integer.MAX_VALUE;
        int max_y = Integer.MIN_VALUE;
        int max_x = Integer.MIN_VALUE;

        if (p0 != null) {
            min_x = p0.x;
            max_x = p0.x;
            min_y = p0.y;
            max_y = p0.y;
        }

        for (IIoletInstanceView i : getInletViews()) {
            Point p1 = i.getJackLocInCanvas();
            min_x = Math.min(min_x, p1.x);
            min_y = Math.min(min_y, p1.y);
            max_x = Math.max(max_x, p1.x);
            max_y = Math.max(max_y, p1.y);
        }
        for (IIoletInstanceView i : getOutletViews()) {
            Point p1 = i.getJackLocInCanvas();
            min_x = Math.min(min_x, p1.x);
            min_y = Math.min(min_y, p1.y);
            max_x = Math.max(max_x, p1.x);
            max_y = Math.max(max_y, p1.y);
        }

        int padding = 5;
        setBounds(min_x - padding, min_y - padding, Math.max(1, max_x - min_x + (2 * padding)),
                (int) calcCtrlPointY(min_x, min_y, max_x, max_y) - min_y + (2 * padding));
        boundsChangedSincePaint = true;

    }

}
