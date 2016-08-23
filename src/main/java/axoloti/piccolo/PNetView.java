package axoloti.piccolo;

import axoloti.INetView;
import axoloti.Net;
import axoloti.PatchViewPiccolo;
import axoloti.Theme;
import axoloti.inlets.IInletInstanceView;
import axoloti.outlets.IOutletInstanceView;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.piccolo2d.util.PPaintContext;

public class PNetView extends PatchPNode implements INetView {

    protected final ArrayList<IOutletInstanceView> source = new ArrayList<>();
    protected final ArrayList<IInletInstanceView> dest = new ArrayList<>();
    protected Net net;
    protected boolean selected = false;

    public PNetView(Net net, PatchViewPiccolo patchView) {
        super(patchView);
        this.net = net;
        setPickable(false);
    }

    @Override
    public void PostConstructor() {
        net.PostConstructor();
    }

    @Override
    public void setSelected(boolean selected) {
        if (this.selected == selected) {
            return;
        }
        this.selected = selected;
        for (IOutletInstanceView i : source) {
            i.setHighlighted(selected);
        }
        for (IInletInstanceView i : dest) {
            i.setHighlighted(selected);
        }
        repaint();
    }

    @Override
    public boolean getSelected() {
        return selected;
    }

    final static float[] dash = {2.f, 4.f};
    final static Stroke strokeValidSelected = new BasicStroke(1.75f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    final static Stroke strokeValidDeselected = new BasicStroke(0.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    final static Stroke strokeBrokenSelected = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, dash, 0.f);
    final static Stroke strokeBrokenDeselected = new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, dash, 0.f);
    final QuadCurve2D.Float curve = new QuadCurve2D.Float();

    float CtrlPointY(float x1, float y1, float x2, float y2) {
        return Math.max(y1, y2) + Math.abs(y2 - y1) * 0.1f + Math.abs(x2 - x1) * 0.1f;
    }

    void DrawWire(Graphics2D g2, float x1, float y1, float x2, float y2) {
        curve.setCurve(x1, y1, (x1 + x2) / 2, CtrlPointY(x1, y1, x2, y2), x2, y2);
        g2.draw(curve);
    }

    @Override
    public void connectInlet(IInletInstanceView inlet) {
        if (inlet == null) {
            throw new RuntimeException("Cannot connect a null InletInstanceView to a NetView.");
        }
        dest.add(inlet);
    }

    @Override
    public void connectOutlet(IOutletInstanceView outlet) {
        if (outlet == null) {
            throw new RuntimeException("Cannot connect a null OutInstanceView to a NetView.");
        }
        source.add(outlet);
    }

    protected Map<IInletInstanceView, QuadCurve2D.Float> inletCurves = new HashMap<>();
    protected Map<IOutletInstanceView, QuadCurve2D.Float> outletCurves = new HashMap<>();

    protected boolean boundsChangedSincePaint = false;

    @Override
    public void updateBounds() {
        int min_y = Integer.MAX_VALUE;
        int min_x = Integer.MAX_VALUE;
        int max_y = Integer.MIN_VALUE;
        int max_x = Integer.MIN_VALUE;

        for (IInletInstanceView i : dest) {
            Point p1 = i.getJackLocInCanvas();
            min_x = Math.min(min_x, p1.x);
            min_y = Math.min(min_y, p1.y);
            max_x = Math.max(max_x, p1.x);
            max_y = Math.max(max_y, p1.y);
        }
        for (IOutletInstanceView i : source) {
            Point p1 = i.getJackLocInCanvas();
            min_x = Math.min(min_x, p1.x);
            min_y = Math.min(min_y, p1.y);
            max_x = Math.max(max_x, p1.x);
            max_y = Math.max(max_y, p1.y);
        }
        setBounds(min_x, min_y, Math.max(1, max_x - min_x),
                (int) CtrlPointY(min_x, min_y, max_x, max_y) - min_y);
        boundsChangedSincePaint = true;
    }

    public void setInletCurve(IInletInstanceView i, Point2D from, Point2D to) {
        QuadCurve2D.Float curve = inletCurves.get(i);
        if (curve == null) {
            curve = new QuadCurve2D.Float();
            inletCurves.put(i, curve);
        }
        setCurve(curve, from, to);
    }

    public void setOutletCurve(IOutletInstanceView i, Point2D from, Point2D to) {
        QuadCurve2D.Float curve = outletCurves.get(i);
        if (curve == null) {
            curve = new QuadCurve2D.Float();
            outletCurves.put(i, curve);
        }
        setCurve(curve, from, to);
    }

    public void setCurve(QuadCurve2D.Float curve, Point2D from, Point2D to) {
        int x1 = (int) from.getX();
        int x2 = (int) to.getX();
        int y1 = (int) from.getY();
        int y2 = (int) to.getY();

        curve.setCurve(x1, y1, (x1 + x2) / 2, CtrlPointY(x1, y1, x2, y2), x2, y2);
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();
        float shadowOffset = 0.5f;
        Point p0;
        Color c;
        boolean isValidNet = net.isValidNet();
        if (isValidNet) {
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

        if (boundsChangedSincePaint) {
            if (isValidNet) {
                p0 = source.get(0).getJackLocInCanvas();
            } else if (!source.isEmpty()) {
                p0 = source.get(0).getJackLocInCanvas();
            } else if (!dest.isEmpty()) {
                p0 = dest.get(0).getJackLocInCanvas();
            } else {
                throw new Error("empty nets should not exist");
            }

            Point2D from = p0;
            // fan out
            if (source.size() == 1) {
                for (IInletInstanceView i : dest) {
                    Point2D to = i.getJackLocInCanvas();
                    setInletCurve(i, from, to);
                }
            } else if (dest.size() == 1) {
                // fan in
                for (IOutletInstanceView i : source) {
                    Point to = i.getJackLocInCanvas();
                    setOutletCurve(i, from, from);
                }
            } else if (source.isEmpty()) {
                // dashed inlet only connection
                for (IInletInstanceView i : dest) {
                    Point2D to = i.getJackLocInCanvas();
                    if (!to.equals(from)) {
                        setInletCurve(i, from, to);
                        from = to;
                    }
                }
            } else {
                // dashed outlet only connection
                // this shouldn't actually happen, but is accepted by isValidNet()
                // here for completeness
                for (IOutletInstanceView i : source) {
                    Point2D to = i.getJackLocInCanvas();
                    if (!to.equals(from)) {
                        setOutletCurve(i, from, to);
                        from = to;
                    }
                }
            }
            boundsChangedSincePaint = false;
        }

        PUtils.setRenderQualityToHigh(g2);

        if (source.size() == 1 || source.isEmpty()) {
            for (IInletInstanceView i : dest) {
                g2.setColor(c);
                if (inletCurves.containsKey(i)) {
                    g2.draw(inletCurves.get(i));
                }
            }
        } else {
            for (IOutletInstanceView i : source) {
                g2.setColor(c);
                if (outletCurves.containsKey(i)) {
                    g2.draw(outletCurves.get(i));
                }
            }
        }

        PUtils.setRenderQualityToLow(g2);
    }

    @Override
    public Net getNet() {
        return net;
    }

    @Override
    public ArrayList<IOutletInstanceView> getSourceViews() {
        return source;
    }

    @Override
    public ArrayList<IInletInstanceView> getDestinationViews() {
        return dest;
    }
}
