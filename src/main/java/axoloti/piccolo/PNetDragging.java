package axoloti.piccolo;

import axoloti.Net;
import axoloti.PatchViewPiccolo;
import axoloti.Theme;
import axoloti.inlets.IInletInstanceView;
import axoloti.outlets.IOutletInstanceView;
import static axoloti.piccolo.PUtils.asPoint;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import org.piccolo2d.util.PPaintContext;

public class PNetDragging extends PNetView {

    public PNetDragging(PatchViewPiccolo patchView) {
        this(patchView.getPatchController().getNetDraggingModel(), patchView);
    }

    public PNetDragging(Net net, PatchViewPiccolo patchView) {
        super(net, patchView);
    }

    Point p0;

    public void SetDragPoint(Point p0) {
        this.p0 = p0;

        updateBounds();
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();
        float shadowOffset = 0.5f;
        Color c;
        if (net.isValidNet()) {
            if (selected) {
                g2.setStroke(strokeValidSelected);
            } else {
                g2.setStroke(strokeValidDeselected);
            }

            c = net.getDataType().GetColor();
        } else {
            if (selected) {
                g2.setStroke(strokeBrokenSelected);
            } else {
                g2.setStroke(strokeBrokenDeselected);
            }

            if (net.getDataType() != null) {
                c = net.getDataType().GetColor();
            } else {
                c = Theme.getCurrentTheme().Cable_Shadow;
            }
        }
        if (p0 != null) {
            if (boundsChangedSincePaint) {
                Point2D from = asPoint(globalToLocal(p0));
                for (IInletInstanceView i : getDestinationViews()) {
                    Point2D to = asPoint(globalToLocal(i.getJackLocInCanvas()));

                    QuadCurve2D.Float curve = inletCurves.get(i);
                    if (curve == null) {
                        curve = new QuadCurve2D.Float();
                        inletCurves.put(i, curve);
                    }
                    int x1 = (int) from.getX();
                    int x2 = (int) to.getX();
                    int y1 = (int) from.getY();
                    int y2 = (int) to.getY();

                    curve.setCurve(x1, y1, (x1 + x2) / 2, CtrlPointY(x1, y1, x2, y2), x2, y2);

                }
                for (IOutletInstanceView i : getSourceViews()) {
                    Point to = asPoint(globalToLocal(i.getJackLocInCanvas()));

                    QuadCurve2D.Float curve = outletCurves.get(i);
                    if (curve == null) {
                        curve = new QuadCurve2D.Float();
                        outletCurves.put(i, curve);
                    }
                    int x1 = (int) from.getX();
                    int x2 = (int) to.getX();
                    int y1 = (int) from.getY();
                    int y2 = (int) to.getY();

                    curve.setCurve(x1, y1, (x1 + x2) / 2, CtrlPointY(x1, y1, x2, y2), x2, y2);
                }
                boundsChangedSincePaint = false;
            }

            PUtils.setRenderQualityToHigh(g2);
            for (IInletInstanceView i : getDestinationViews()) {
                g2.setColor(c);
                g2.draw(inletCurves.get(i));
            }
            for (IOutletInstanceView i : getSourceViews()) {
                g2.setColor(c);
                g2.draw(outletCurves.get(i));
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

        for (IInletInstanceView i : getDestinationViews()) {
            Point p1 = i.getJackLocInCanvas();
            min_x = Math.min(min_x, p1.x);
            min_y = Math.min(min_y, p1.y);
            max_x = Math.max(max_x, p1.x);
            max_y = Math.max(max_y, p1.y);
        }
        for (IOutletInstanceView i : getSourceViews()) {
            Point p1 = i.getJackLocInCanvas();
            min_x = Math.min(min_x, p1.x);
            min_y = Math.min(min_y, p1.y);
            max_x = Math.max(max_x, p1.x);
            max_y = Math.max(max_y, p1.y);
        }

        int padding = 5;
        setBounds(min_x - padding, min_y - padding, Math.max(1, max_x - min_x + (2 * padding)),
                (int) CtrlPointY(min_x, min_y, max_x, max_y) - min_y + (2 * padding));
        boundsChangedSincePaint = true;

    }

    @Override
    public void connectInlet(IInletInstanceView inlet) {
        if (inlet == null) {
            throw new RuntimeException("Cannot connect a null InletInstanceView to a NetView.");
        }
        dest.add(inlet);
        net.connectInlet(inlet.getInletInstance());
    }

    @Override
    public void connectOutlet(IOutletInstanceView outlet) {
        if (outlet == null) {
            throw new RuntimeException("Cannot connect a null OutInstanceView to a NetView.");
        }
        source.add(outlet);
        net.connectOutlet(outlet.getOutletInstance());
    }
}
