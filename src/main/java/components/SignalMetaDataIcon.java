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
import axoloti.datatypes.SignalMetaData;
import static axoloti.datatypes.SignalMetaData.bipolar;
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
public class SignalMetaDataIcon extends JComponent {

    private final SignalMetaData smd;

    public SignalMetaDataIcon(SignalMetaData smd) {
        this.smd = smd;
        Dimension d = new Dimension(12, 14);
        setMinimumSize(d);
        setMaximumSize(d);
        setPreferredSize(d);
        setBackground(Theme.getCurrentTheme().Object_Default_Background);
    }
    private final int x1 = 2;
    private final int x2 = 5;
    private final int x2_5 = 7;
    private final int x3 = 9;
    private final int x4 = 12;
    private final int y1 = 12;
    private final int y2 = 2;
    private static final Stroke stroke = new BasicStroke(1.0f);
//    private static final Stroke strokeThick = new BasicStroke(2.5f);

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(stroke);
        switch (smd) {
            case rising:
                g2.setColor(getForeground());
                g2.drawLine(x1, y1, x2_5, y1); // _
                g2.drawLine(x2_5, y1, x2_5, y2); // /
                g2.drawLine(x2_5, y2, x4, y2); // -
                break;
            case falling:
                g2.setColor(getForeground());
                g2.drawLine(x1, y2, x2_5, y2); // _
                g2.drawLine(x2_5, y1, x2_5, y2); // /
                g2.drawLine(x2_5, y1, x4, y1); // -
                break;
            case risingfalling:
                g2.setColor(getForeground());
                g2.drawLine(x1, y1, x2, y1); // _
                g2.drawLine(x2, y2, x3, y2); // -
                g2.drawLine(x3, y1, x4, y1); // _
                g2.drawLine(x2, y1, x2, y2); // /
                g2.drawLine(x3, y2, x3, y1); // \
                break;
            case pulse:
                g2.setColor(getForeground());
                g2.drawLine(x1, y1, x4, y1); // __
                g2.drawLine(x2_5, y1, x2_5, y2); // |
                break;
            case bipolar:
                g2.setColor(getForeground());
                g2.drawLine(6, 2, 6, 8); // verti
                g2.drawLine(3, 5, 9, 5); // hori

                g2.drawLine(3, 10, 9, 10); // hori
/*
                 g2.drawLine(6, 3, 6, 7); // verti
                 g2.drawLine(4, 5, 8, 5); // hori
                 g2.drawLine(4, 9, 8, 9); // hori
                 */
                break;
            case positive:
                g2.setColor(getForeground());
                g2.drawLine(6, 4, 6, 10); // verti
                g2.drawLine(3, 7, 9, 7); // hori
                break;
            default:
                break;
        }
    }
}
