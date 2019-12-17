/**
 * Copyright (C) 2013, 2014, 2015 Johannes Taelman
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
package axoloti.swingui.components.control;

import axoloti.preferences.Theme;
import java.awt.Dimension;
import java.awt.Graphics2D;

/**
 *
 * @author Johannes Taelman
 */
public class VRadioComponent extends HRadioComponent {

    private int bsize = 14;

    public VRadioComponent(int value, int n) {
        super(value, n);
    }

    @Override
    int mousePosToVal(int x, int y) {
        int i = y / bsize;
        if (i < 0) {
            return 0;
        }
        if (i > getMax() - 1) {
            return getMax() - 1;
        }
        return i;
    }

    @Override
    void paintComponent1(Graphics2D g2) {
        if (isEnabled()) {
            g2.setColor(Theme.getCurrentTheme().Component_Secondary);
        } else {
            g2.setColor(Theme.getCurrentTheme().Object_Default_Background);
        }
        for (int i = 0; i < getMax(); i++) {
            g2.fillOval(0, i * bsize, bsize, bsize);
        }

        g2.setPaint(getForeground());
        if (isFocusOwner()) {
            g2.setStroke(strokeThick);
        }

        for (int i = 0; i < getMax(); i++) {
            g2.drawOval(0, i * bsize, bsize, bsize);
        }

        if (isEnabled()) {
            g2.fillOval(2, (int) getValue() * bsize + 2, bsize - 3, bsize - 3);
        }
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(bsize + 2, bsize * getMax() + 2);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(bsize + 2, bsize * getMax() + 2);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(bsize + 2, bsize * getMax() + 2);
    }
}
