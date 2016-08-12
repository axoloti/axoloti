package axoloti;

import axoloti.inlets.InletInstanceView;
import axoloti.outlets.OutletInstanceView;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class NetView extends JComponent {

    ArrayList<OutletInstanceView> source = new ArrayList<OutletInstanceView>();
    ArrayList<InletInstanceView> dest = new ArrayList<InletInstanceView>();

    Net net;
    boolean selected = false;

    PatchView patchView;

    NetView(Net net, PatchView patchView) {
        this.net = net;
        this.patchView = patchView;

        setSize(1, 1);
        setLocation(0, 0);
        setOpaque(false);
    }
    
    public void PostConstructor() {
        this.net.PostConstructor();
    }

    public void setSelected(boolean selected) {
        if (this.selected == selected) {
            return;
        }
        this.selected = selected;
        for (OutletInstanceView i : source) {
            i.setHighlighted(selected);
        }
        for (InletInstanceView i : dest) {
            i.setHighlighted(selected);
        }
        repaint();
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean getSelected() {
        return this.selected;
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

    public void connectInlet(InletInstanceView inlet) {
        if(inlet == null) {
            throw new RuntimeException("Cannot connect a null InletInstanceView to a NetView.");
        }
        dest.add(inlet);
    }

    public void connectOutlet(OutletInstanceView outlet) {
        if(outlet == null) {
            throw new RuntimeException("Cannot connect a null OutInstanceView to a NetView.");
        }
        source.add(outlet);
    }

    public void updateBounds() {
        int min_y = Integer.MAX_VALUE;
        int min_x = Integer.MAX_VALUE;
        int max_y = Integer.MIN_VALUE;
        int max_x = Integer.MIN_VALUE;

        for (InletInstanceView i : dest) {
            Point p1 = i.getJackLocInCanvas();
            min_x = Math.min(min_x, p1.x);
            min_y = Math.min(min_y, p1.y);
            max_x = Math.max(max_x, p1.x);
            max_y = Math.max(max_y, p1.y);
        }
        for (OutletInstanceView i : source) {
            Point p1 = i.getJackLocInCanvas();
            min_x = Math.min(min_x, p1.x);
            min_y = Math.min(min_y, p1.y);
            max_x = Math.max(max_x, p1.x);
            max_y = Math.max(max_y, p1.y);
        }
        int fudge = 8;
        this.setBounds(min_x - fudge, min_y - fudge,
                Math.max(1, max_x - min_x + (2 * fudge)),
                (int) CtrlPointY(min_x, min_y, max_x, max_y) - min_y + (2 * fudge));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        float shadowOffset = 0.5f;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        Point p0;
        Color c;
        if (net.isValidNet()) {
            if (selected) {
                g2.setStroke(strokeValidSelected);
            } else {
                g2.setStroke(strokeValidDeselected);
            }

            c = net.getDataType().GetColor();
            p0 = source.get(0).getJackLocInCanvas();
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

            if (!source.isEmpty()) {
                p0 = source.get(0).getJackLocInCanvas();
            } else if (!dest.isEmpty()) {
                p0 = dest.get(0).getJackLocInCanvas();
            } else {
                throw new Error("empty nets should not exist");
            }
        }

        Point from = SwingUtilities.convertPoint(patchView.Layers, p0, this);
        for (InletInstanceView i : dest) {
            Point p1 = i.getJackLocInCanvas();

            Point to = SwingUtilities.convertPoint(patchView.Layers, p1, this);
            g2.setColor(Theme.getCurrentTheme().Cable_Shadow);
            DrawWire(g2, from.x + shadowOffset, from.y + shadowOffset, to.x + shadowOffset, to.y + shadowOffset);
            g2.setColor(c);
            DrawWire(g2, from.x, from.y, to.x, to.y);
        }
        for (OutletInstanceView i : source) {
            Point p1 = i.getJackLocInCanvas();

            Point to = SwingUtilities.convertPoint(patchView.Layers, p1, this);
            g2.setColor(Theme.getCurrentTheme().Cable_Shadow);
            DrawWire(g2, from.x + shadowOffset, from.y + shadowOffset, to.x + shadowOffset, to.y + shadowOffset);
            g2.setColor(c);
            DrawWire(g2, from.x, from.y, to.x, to.y);

        }
    }
    
    public Net getNet() {
        return this.net;
    }
}