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
package axoloti;

import axoloti.inlets.InletInstance;
import axoloti.outlets.OutletInstance;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

/**
 *
 * @author Johannes Taelman
 */
public class NetDragging extends Net {

    public NetDragging(Patch patch) {
        super(patch);
    }

    Point p0;

    public void SetDragPoint(Point p0) {
        this.p0 = p0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        float shadowOffset = 0.5f;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        Color c;
        if (isValidNet()) {
            if (selected) {
                g2.setStroke(strokeValidSelected);
            } else {
                g2.setStroke(strokeValidDeselected);
            }

            c = GetDataType().GetColor();
        } else {
            if (selected) {
                g2.setStroke(strokeBrokenSelected);
            } else {
                g2.setStroke(strokeBrokenDeselected);
            }

            if (GetDataType() != null) {
                c = GetDataType().GetColor();
            } else {
                c = Color.BLACK;
            }
        }
        int lastSource = 0;
        for (OutletInstance i : source) {
//  Indicate latched connections
            int j = patch.objectinstances.indexOf(i.GetObjectInstance());
            if (j > lastSource) {
                lastSource = j;
            }
            Point p1 = i.getJackLocInCanvas();
            g2.setColor(Color.BLACK);
            DrawWire(g2, p0.x + shadowOffset, p0.y + shadowOffset, p1.x + shadowOffset, p1.y + shadowOffset);
            g2.setColor(c);
            DrawWire(g2, p0.x, p0.y, p1.x, p1.y);
        }
        for (InletInstance i : dest) {
            Point p1 = i.getJackLocInCanvas();
            g2.setColor(Color.BLACK);
            DrawWire(g2, p0.x + shadowOffset, p0.y + shadowOffset, p1.x + shadowOffset, p1.y + shadowOffset);
            g2.setColor(c);
            DrawWire(g2, p0.x, p0.y, p1.x, p1.y);
//  Indicate latched connections
//            if (false) {
//                int j = patch.objectinstances.indexOf(i.axoObj);
//                if (j <= lastSource) {
//                    int x = (p0.x + p1.x) / 2;
//                    int y = (int) (0.5f * (p0.y + p1.y) + Math.abs(p1.y - p0.y) * 0.3f + Math.abs(p1.x - p0.x) * 0.05f);
//                    g2.fillOval(x - 5, y - 5, 10, 10);
//                }
//            }
        }
    }

}
