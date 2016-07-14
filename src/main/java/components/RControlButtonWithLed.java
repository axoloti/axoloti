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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 * @author Johannes Taelman
 */
public class RControlButtonWithLed extends Component {

    private static final int arc = 10;
    private static final int inset = 5;

    public RControlButtonWithLed() {
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!e.isShiftDown()) {
                    pressed = false;
                    repaint();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }
    private boolean pressed = false;
    private boolean illuminated = false;

    public void setIlluminated(boolean illuminated) {
        if (this.illuminated != illuminated) {
            this.illuminated = illuminated;
            repaint();
        }
    }

    @Override
    public void paint(Graphics g) {
        int height = getHeight();
        int width = getWidth();
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(Theme.getCurrentTheme().Object_Default_Background);
        g2.fillRect(0, 0, width, height);
        if (illuminated) {
            g2.setPaint(Theme.getCurrentTheme().Component_Illuminated);
            g2.fillRoundRect(0, 0, width, height, arc, arc);
        }
        g2.setPaint(Theme.getCurrentTheme().Component_Primary);
        if (pressed) {
            g2.fillRoundRect(inset, inset, width - inset * 2, height - inset * 2, arc, arc);
        } else {
            g2.drawRoundRect(inset, inset, width - inset * 2, height - inset * 2, arc, arc);
        }
    }
}
