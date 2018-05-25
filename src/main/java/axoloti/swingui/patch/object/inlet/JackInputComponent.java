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
package axoloti.swingui.patch.object.inlet;

import axoloti.preferences.Theme;
import java.awt.BasicStroke;
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
class JackInputComponent extends JComponent {

    private static final int sz = 10;
    private static final int margin = 2;
    private static final Dimension dim = new Dimension(sz, sz);

    private boolean connected = false;

    JackInputComponent() {
        initComponent();
    }

    private void initComponent() {
        setMinimumSize(dim);
        setMaximumSize(dim);
        setPreferredSize(dim);
        setSize(dim);
        setAlignmentY(CENTER_ALIGNMENT);
        setAlignmentX(RIGHT_ALIGNMENT);
    }

    private final static Stroke stroke = new BasicStroke(1.5f);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(stroke);
        if (connected) {
            g2.setPaint(Theme.getCurrentTheme().Component_Primary);
            g2.drawOval(margin + 1, margin + 1, sz - margin - margin, sz - margin - margin);
            g2.setPaint(getForeground());
            g2.fillOval(margin, margin, sz - margin - margin, sz - margin - margin);
            g2.drawOval(margin, margin, sz - margin - margin, sz - margin - margin);
        } else {
            g2.setPaint(Theme.getCurrentTheme().Component_Primary);
            g2.drawOval(margin + 1, margin + 1, sz - margin - margin, sz - margin - margin);
            g2.setPaint(getForeground());
            g2.drawOval(margin, margin, sz - margin - margin, sz - margin - margin);
        }
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
        repaint();
    }
}
