/**
 * Copyright (C) 2013, 2014 Johannes Taelman
 *
 * This file is part of Axoloti.
 *
 * Axoloti is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Axoloti is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Axoloti. If not, see <http://www.gnu.org/licenses/>.
 */
package axoloti.swingui.patch.net;

import axoloti.abstractui.IInletInstanceView;
import axoloti.abstractui.IIoletInstanceView;
import axoloti.abstractui.IOutletInstanceView;
import axoloti.patch.net.Net;
import axoloti.preferences.Theme;
import axoloti.swingui.patch.PatchViewSwing;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.SwingUtilities;

/**
 *
 * @author Johannes Taelman
 */
public class NetDragging extends NetView {

    public NetDragging(Net net, PatchViewSwing patchView) {
        super(net, patchView);
    }

    private Point p0;

    public void setDragPoint(Point p0) {
        this.p0 = p0;
        PatchViewSwing pv = getPatchView();
        if (pv != null) {
            Rectangle r = new Rectangle(p0.x, p0.y, 1, 1);
            pv.scrollTo(r);
        }
        updateBounds();
        repaint();
    }

    @Override
    void paint1(Graphics g) {
        float shadowOffset = 0.5f;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        Color c;
        if (getDModel().isValidNet()) {
            if (getSelected()) {
                g2.setStroke(strokeValidSelected);
            } else {
                g2.setStroke(strokeValidDeselected);
            }

            c = getDModel().getDataType().getColor();
        } else {
            if (getSelected()) {
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
            Point from = SwingUtilities.convertPoint(getPatchView().layers, p0, this);
            for (IIoletInstanceView i : getInletViews()) {
                Point p1 = i.getJackLocInCanvas();
                Point to = SwingUtilities.convertPoint(getPatchView().layers, p1, this);
                g2.setColor(Theme.getCurrentTheme().Cable_Shadow);
                drawWire(g2, from.x + shadowOffset, from.y + shadowOffset, to.x + shadowOffset, to.y + shadowOffset);
                g2.setColor(c);
                drawWire(g2, from.x, from.y, to.x, to.y);
            }
            for (IIoletInstanceView i : getOutletViews()) {
                Point p1 = i.getJackLocInCanvas();
                Point to = SwingUtilities.convertPoint(getPatchView().layers, p1, this);
                g2.setColor(Theme.getCurrentTheme().Cable_Shadow);
                drawWire(g2, from.x + shadowOffset, from.y + shadowOffset, to.x + shadowOffset, to.y + shadowOffset);
                g2.setColor(c);
                drawWire(g2, from.x, from.y, to.x, to.y);
            }
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

        for (IInletInstanceView i : getInletViews()) {
            Point p1 = i.getJackLocInCanvas();
            min_x = Math.min(min_x, p1.x);
            min_y = Math.min(min_y, p1.y);
            max_x = Math.max(max_x, p1.x);
            max_y = Math.max(max_y, p1.y);
        }
        for (IOutletInstanceView i : getOutletViews()) {
            Point p1 = i.getJackLocInCanvas();
            min_x = Math.min(min_x, p1.x);
            min_y = Math.min(min_y, p1.y);
            max_x = Math.max(max_x, p1.x);
            max_y = Math.max(max_y, p1.y);
        }

        int padding = 8;
        setBounds(min_x - padding, min_y - padding,
                Math.max(1, max_x - min_x + (2 * padding)),
                (int) calcCtrlPointY(min_x, min_y, max_x, max_y) - min_y + (2 * padding));
    }

}
