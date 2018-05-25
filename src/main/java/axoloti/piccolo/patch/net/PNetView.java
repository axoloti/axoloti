package axoloti.piccolo.patch.net;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.abstractui.IInletInstanceView;
import axoloti.abstractui.IIoletInstanceView;
import axoloti.abstractui.INetView;
import axoloti.abstractui.IOutletInstanceView;
import axoloti.patch.net.Net;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.piccolo.PUtils;
import axoloti.piccolo.patch.PatchPNode;
import axoloti.piccolo.patch.PatchViewPiccolo;
import axoloti.preferences.Theme;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.piccolo2d.util.PPaintContext;

public class PNetView extends PatchPNode implements INetView {

    protected final List<IOutletInstanceView> source = new ArrayList<>();
    protected final List<IInletInstanceView> dest = new ArrayList<>();
    protected boolean selected = false;

    Net net;

    public PNetView(Net net, PatchViewPiccolo patchView) {
        super(patchView);
        this.net = net;
        setPickable(false);
        updateSources();
        updateDests();
        initComponent();
    }

    private void initComponent() {
        updateBounds();
    }

    private void updateSources() {
        source.clear();
        // resolve inlet/outlet views
        for (OutletInstance i : getDModel().getSources()) {
            IAxoObjectInstance o = i.getParent();
            IAxoObjectInstanceView ov = patchView.findObjectInstanceView(o);
            if (ov == null) {
                throw new Error("no corresponding outlet instance view found");
            }
            for (IOutletInstanceView o2 : ov.getOutletInstanceViews()) {
                if (o2.getDModel() == i) {
                    source.add(o2);
                    break;
                }
            }
        }
    }

    private void updateDests() {
        dest.clear();
        for (InletInstance i : getDModel().getDestinations()) {
            IAxoObjectInstance o = i.getParent();
            IAxoObjectInstanceView ov = patchView.findObjectInstanceView(o);
            if (ov == null) {
                throw new Error("no corresponding inlet instance view found");
            }
            for (IInletInstanceView o2 : ov.getInletInstanceViews()) {
                if (o2.getDModel() == i) {
                    dest.add(o2);
                    break;
                }
            }
        }
    }

    @Override
    public void setSelected(boolean selected) {
        if (this.selected == selected) {
            return;
        }
        this.selected = selected;
        for (IIoletInstanceView i : source) {
            i.setHighlighted(selected);
        }
        for (IIoletInstanceView i : dest) {
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

    float calcCtrlPointY(float x1, float y1, float x2, float y2) {
        return Math.max(y1, y2) + Math.abs(y2 - y1) * 0.1f + Math.abs(x2 - x1) * 0.1f;
    }

    void drawWire(Graphics2D g2, float x1, float y1, float x2, float y2) {
        curve.setCurve(x1, y1, (x1 + x2) / 2, calcCtrlPointY(x1, y1, x2, y2), x2, y2);
        g2.draw(curve);
    }

    protected Map<IIoletInstanceView, QuadCurve2D.Float> ioletCurves = new HashMap<>();

    protected boolean boundsChangedSincePaint = false;

    @Override
    public void updateBounds() {
        int min_y = Integer.MAX_VALUE;
        int min_x = Integer.MAX_VALUE;
        int max_y = Integer.MIN_VALUE;
        int max_x = Integer.MIN_VALUE;

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

        setBounds(min_x, min_y, Math.max(1, max_x - min_x),
                (int) calcCtrlPointY(min_x, min_y, max_x, max_y) - min_y);
        boundsChangedSincePaint = true;
    }

    public QuadCurve2D.Float getIoletCurve(IIoletInstanceView i) {
        QuadCurve2D.Float curve = ioletCurves.get(i);
        if (curve == null) {
            curve = new QuadCurve2D.Float();
            ioletCurves.put(i, curve);
        }
        return curve;
    }

    public void setCurveShape(QuadCurve2D.Float curve, Point2D from, Point2D to) {
        int x1 = (int) from.getX();
        int x2 = (int) to.getX();
        int y1 = (int) from.getY();
        int y2 = (int) to.getY();

        curve.setCurve(x1, y1, (x1 + x2) / 2.0, calcCtrlPointY(x1, y1, x2, y2), x2, y2);
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();
        float shadowOffset = 0.5f;
        Point p0;
        Color c;
        boolean isValidNet = getDModel().isValidNet();
        if (isValidNet) {
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
                for (IIoletInstanceView i : dest) {
                    Point2D to = i.getJackLocInCanvas();
                    setCurveShape(getIoletCurve(i), from, to);
                }
            } else if (dest.size() == 1) {
                // fan in
                for (IIoletInstanceView i : source) {
                    Point to = i.getJackLocInCanvas();
                    setCurveShape(getIoletCurve(i), from, to);
                }
            } else if (source.isEmpty()) {
                // dashed inlet only connection
                for (IIoletInstanceView i : dest) {
                    Point2D to = i.getJackLocInCanvas();
                    if (!to.equals(from)) {
                        setCurveShape(getIoletCurve(i), from, to);
                        from = to;
                    }
                }
            } else {
                // dashed outlet only connection
                // this shouldn't actually happen, but is accepted by isValidNet()
                // here for completeness
                for (IIoletInstanceView i : source) {
                    Point2D to = i.getJackLocInCanvas();
                    if (!to.equals(from)) {
                        setCurveShape(getIoletCurve(i), from, to);
                        from = to;
                    }
                }
            }
            boundsChangedSincePaint = false;
        }

        PUtils.setRenderQualityToHigh(g2);

        if (source.size() == 1 || source.isEmpty()) {
            for (IIoletInstanceView i : dest) {
                g2.setColor(c);
                if (ioletCurves.containsKey(i)) {
                    g2.draw(ioletCurves.get(i));
                }
            }
        } else {
            for (IIoletInstanceView i : source) {
                g2.setColor(c);
                if (ioletCurves.containsKey(i)) {
                    g2.draw(ioletCurves.get(i));
                }
            }
        }

        PUtils.setRenderQualityToLow(g2);
    }

    @Override
    public List<IInletInstanceView> getInletViews() {
        return Collections.unmodifiableList(dest);
    }

    @Override
    public List<IOutletInstanceView> getOutletViews() {
        return Collections.unmodifiableList(source);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (Net.NET_SOURCES.is(evt)) {
            updateSources();
            updateBounds();
            repaint();
        } else if (Net.NET_DESTINATIONS.is(evt)) {
            updateDests();
            updateBounds();
            repaint();
        }
    }

    @Override
    public void dispose() {
    }

    @Override
    public Net getDModel() {
        return net;
    }
}
