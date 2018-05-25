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
package axoloti.swingui.components;

import axoloti.preferences.Theme;
import axoloti.utils.Constants;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;

/**
 *
 * @author Johannes Taelman
 */
public abstract class PianoComponent extends JComponent {

    private static final int KEY_HEIGHT = 31;
    private static final int BLACK_KEY_HEIGHT = KEY_HEIGHT / 2;
    private static final int QUARTER_KEY_WIDTH = 3;
    private static final int KEY_WIDTH = QUARTER_KEY_WIDTH * 2;

    private static final int KEY_X[] = {0, 3, 4, 7, 8, 12, 15, 16, 19, 20, 23, 24};
    private static final int KEY_Y[] = {0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0};

    private final boolean selection[] = new boolean[128];

    private int mouseDownNote;

    public PianoComponent() {
        super();
        initComponent();
    }

    private void initComponent() {
        MouseInputAdapter ma = new MouseInputAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == 1) {
                    int i = findKeyFromCoordinates(e.getX(), e.getY());
                    if (i >= 0) {
                        mouseDownNote = i;
                        selection[i] = true;
                        keyDown(i);
                        repaint();
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int i = findKeyFromCoordinates(e.getX(), e.getY());
                if (i != mouseDownNote) {
                    selection[mouseDownNote] = false;
                    keyUp(mouseDownNote);
                    if (i >= 0) {
                        selection[i] = true;
                        mouseDownNote = i;
                        keyDown(i);
                    }
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isShiftDown() == false) {
                    selection[mouseDownNote] = false;
                    keyUp(mouseDownNote);
                    repaint();
                }
            }

        };

        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    public abstract void keyUp(int key);

    public abstract void keyDown(int key);

    public void clear() {
        for (int i=0;i<selection.length;i++) {
            selection[i] = false;
        }
        repaint();
    }

    private int keyToX(int i) {
        return (28 * (i / 12) + KEY_X[i % 12]) * QUARTER_KEY_WIDTH;
    }

    private int keyToY(int i) {
        return KEY_Y[i % 12];
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        // white keys first
        for (int i = 0; i < 128; i++) {
            int y = keyToY(i);
            int x = keyToX(i);
            if (y == 0) {
                if (selection[i]) {
                    // selected
                    g2.setColor(Theme.getCurrentTheme().Keyboard_Mid);
                    g2.fillRect(x, 0, 2 * KEY_WIDTH, KEY_HEIGHT);
                    if (isEnabled()) {
                        g2.setColor(Theme.getCurrentTheme().Keyboard_Dark);
                    } else {
                        g2.setColor(Theme.getCurrentTheme().Keyboard_Mid);
                    }
                    g2.drawRect(x, 0, 2 * KEY_WIDTH, KEY_HEIGHT);
                } else {
                    // not selected
                    g2.setColor(Theme.getCurrentTheme().Keyboard_Light);
                    g2.fillRect(x, 0, 2 * KEY_WIDTH, KEY_HEIGHT);
                    if (isEnabled()) {
                        g2.setColor(Theme.getCurrentTheme().Keyboard_Dark);
                    } else {
                        g2.setColor(Theme.getCurrentTheme().Keyboard_Mid);
                    }
                    g2.drawRect(x, 0, 2 * KEY_WIDTH, KEY_HEIGHT);
                }
            }
        }
        // black keys
        for (int i = 0; i < 128; i++) {
            int y = keyToY(i);
            int x = keyToX(i);
            if (y == 1) {
                if (selection[i]) {
                    g2.setColor(Theme.getCurrentTheme().Keyboard_Mid);
                    g2.fillRect(x - 1, 0, KEY_WIDTH + 2, BLACK_KEY_HEIGHT);
                    if (isEnabled()) {
                        g2.setColor(Theme.getCurrentTheme().Keyboard_Dark);
                    } else {
                        g2.setColor(Theme.getCurrentTheme().Keyboard_Mid);
                    }
                    g2.drawRect(x - 1, 0, KEY_WIDTH + 2, BLACK_KEY_HEIGHT);
                } else {
                    if (isEnabled()) {
                        g2.setColor(Theme.getCurrentTheme().Keyboard_Dark);
                    } else {
                        g2.setColor(Theme.getCurrentTheme().Keyboard_Mid);
                    }
                    g2.fillRect(x - 1, 0, KEY_WIDTH + 2, BLACK_KEY_HEIGHT);
                    g2.drawRect(x - 1, 0, KEY_WIDTH + 2, BLACK_KEY_HEIGHT);
                }
            }
        }
        for (int i = 0; i < 128; i++) {
            if (i % 12 == 0) {
                int x = keyToX(i);
                g2.setFont(Constants.FONT);
                g2.drawString("" + ((i / 12) - 1), x + 2, KEY_HEIGHT - 2);
            }
        }
        for (int i = 0; i < 128; i++) {
            int y = keyToY(i);
            int x = keyToX(i);
            if (y == 0) {
                g2.setFont(Constants.FONT);
                AffineTransform t = g2.getTransform();
                g2.rotate(-3.14159 / 2);
                g2.drawString(String.format("%d", i - 64), -53, x + 9);
                //            g2.drawString(String.format("%.1f", 440*Math.pow(2.0,(i-69.0)/12)), 33 ,-x-1);
                g2.setTransform(t);
            }
        }
        int x = keyToX(64);
        g2.setColor(Theme.getCurrentTheme().Keyboard_Mid);
        g2.fillOval(x + 2, KEY_HEIGHT - (KEY_WIDTH + 2), KEY_WIDTH, KEY_WIDTH);
        g2.dispose();
    }

    int findKeyFromCoordinates(int x, int y) {
        if (!isEnabled()) {
            return -1;
        }
        int o = 12 * (x / (28 * QUARTER_KEY_WIDTH));
        int oe = o + 12;
        if (oe > 128) {
            oe = 128;
        }
        if (y < BLACK_KEY_HEIGHT) {
            // test black keys first
            for (int i = o; i < oe; i++) {
                int iy = keyToY(i);
                if (iy != 0) {
                    int ix = keyToX(i);
                    if ((x >= ix - 1) && (x <= ix + KEY_WIDTH + 1)) {
                        return i;
                    }
                }
            }
        }
        for (int i = o; i < oe; i++) {
            int iy = keyToY(i);
            // white key test
            if (iy == 0) {
                int ix = keyToX(i);
                if ((x >= ix) && (x <= ix + KEY_WIDTH * 2)) {
                    return i;
                }
            }
        }
        return -1;
    }
}
