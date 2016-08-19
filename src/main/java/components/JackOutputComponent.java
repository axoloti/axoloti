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
package components;

import axoloti.Theme;
import axoloti.outlets.OutletInstance;
import java.awt.BasicStroke;
import static java.awt.Component.CENTER_ALIGNMENT;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import javax.swing.JComponent;

/**
 *
 * @author Johannes Taelman
 */
public class JackOutputComponent extends JComponent {

    private static final int sz = 10;
    private static final int margin = 2;
    private static final int inset = 1;
    private static final Dimension dim = new Dimension(sz, sz);
    final OutletInstance outlet;

    public JackOutputComponent(OutletInstance outlet) {
        setMinimumSize(dim);
        setMaximumSize(dim);
        setPreferredSize(dim);
        setSize(dim);
        setAlignmentY(CENTER_ALIGNMENT);
        setAlignmentX(RIGHT_ALIGNMENT);
        this.outlet = outlet;
    }
    private final static Stroke stroke = new BasicStroke(1.5f);

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(stroke);
        g2.setPaint(Theme.getCurrentTheme().Component_Primary);
        g2.drawRect(margin, margin + 1, sz - margin - margin, sz - margin - margin);

        if (outlet.isConnected()) {
            g2.fillOval(margin + inset, margin + inset + 1, sz - (margin + inset) * 2 + 1, sz - (margin + inset) * 2 + 1);
            g2.setPaint(getForeground());
            g2.fillOval(margin + inset - 1, margin + inset, sz - (margin + inset) * 2 + 1, sz - (margin + inset) * 2 + 1);
        } else {
            g2.drawOval(margin + inset, margin + inset + 1, sz - (margin + inset) * 2, sz - (margin + inset) * 2);
            g2.setPaint(getForeground());
            g2.drawOval(margin + inset - 1, margin + inset, sz - (margin + inset) * 2, sz - (margin + inset) * 2);
        }

        g2.drawRect(margin - 1, margin, sz - margin - margin, sz - margin - margin);
    }
    
    public OutletInstance getOutlet() {
        return outlet;
    }
}
