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

import axoloti.preferences.Preferences;
import axoloti.preferences.Theme;
import axoloti.swingui.TransparentCursor;
import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;

/**
 *
 * @author Johannes Taelman
 */
public abstract class RControlEncoder extends JComponent {

    private int mousePressedCoordX = 0;
    private int mousePressedCoordY = 0;
    private int mousePressedBtn = 0;
    private Robot robot;

    public RControlEncoder() {
        try {
            if (Preferences.getPreferences().getMouseDoNotRecenterWhenAdjustingControls()) {
                robot = null;
            } else {
                robot = new Robot(MouseInfo.getPointerInfo().getDevice());
            }
        } catch (AWTException ex) {
            Logger.getLogger(RControlEncoder.class.getName()).log(Level.SEVERE, null, ex);
        }

        initComponent();
    }

    private void initComponent() {
        MouseAdapter mouseAdapter = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                grabFocus();
                mousePressedCoordX = e.getXOnScreen();
                mousePressedCoordY = e.getYOnScreen();
                mousePressedBtn = e.getButton();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                getRootPane().setCursor(Cursor.getDefaultCursor());
                robot = null;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if ((mousePressedBtn == MouseEvent.BUTTON1)) {
                    int v;
                    v = (mousePressedCoordY - e.getYOnScreen());
                    if (Math.abs(v) > 2) {
                        if (robot != null) {
                            getRootPane().setCursor(TransparentCursor.get());
                            robot.mouseMove(mousePressedCoordX, mousePressedCoordY);
                        } else {
                            mousePressedCoordY = e.getYOnScreen();
                        }
                        fireRotation1(v / 2);
                    }
                }
            }

        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }
    private double angle = 0;

    private void fireRotation1(int ticks) {
        if (ticks != 0) {
            angle += Math.PI * ticks / 40.0;
            repaint();
            fireRotation(ticks);
        }
    }

    protected abstract void fireRotation(int ticks);

    @Override
    public void paint(Graphics g) {
        int height = getHeight();
        int width = getWidth();

        int diameter = (height > width ? width : height) - 2;
        diameter = (diameter / 4) * 4;
        int hoffset = (width - diameter) / 2;
        int voffset = (height - diameter) / 2;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(Theme.getCurrentTheme().Object_Default_Background);
        g2.fillRect(0, 0, width, height);
        g2.setPaint(Theme.getCurrentTheme().Component_Primary);
        g2.drawOval(hoffset, voffset, diameter, diameter);

        g2.fillOval(hoffset + diameter / 4, voffset + diameter / 4, diameter / 2, diameter / 2);

        double hcenter = 0.5 * width;
        double vcenter = 0.5 * height;
        double radius = 0.5 * diameter - 0.5;
        int divs = 10;
        for (int i = 0; i < divs; i++) {
            double angle2 = angle + (2 * Math.PI * i / (float) divs);
            g2.draw(new Line2D.Double(hcenter + radius * Math.cos(angle2), vcenter + radius * Math.sin(angle2),
                    hcenter - radius * Math.cos(angle2), vcenter - radius * Math.sin(angle2)));
        }
    }
}
