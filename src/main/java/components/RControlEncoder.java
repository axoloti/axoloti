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

import axoloti.MainFrame;
import axoloti.Theme;
import axoloti.utils.Preferences;
import components.control.DialComponent;
import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;

/**
 *
 * @author Johannes Taelman
 */
public abstract class RControlEncoder extends JComponent {

    int MousePressedCoordX = 0;
    int MousePressedCoordY = 0;
    int MousePressedBtn = 0;
    Robot robot;

    public RControlEncoder() {
        try {
            if (Preferences.LoadPreferences().getMouseDoNotRecenterWhenAdjustingControls()) {
                robot = null;
            } else {
                robot = new Robot(MouseInfo.getPointerInfo().getDevice());
            }
        } catch (AWTException ex) {
            Logger.getLogger(DialComponent.class.getName()).log(Level.SEVERE, null, ex);
        }
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                grabFocus();
                MousePressedCoordX = e.getXOnScreen();
                MousePressedCoordY = e.getYOnScreen();
                MousePressedBtn = e.getButton();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                getRootPane().setCursor(Cursor.getDefaultCursor());
                robot = null;
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if ((MousePressedBtn == MouseEvent.BUTTON1)) {
                    int v;
                    v = (MousePressedCoordY - e.getYOnScreen());
                    if (Math.abs(v) > 2) {
                        if (robot != null) {
                            getRootPane().setCursor(MainFrame.transparentCursor);
                            robot.mouseMove(MousePressedCoordX, MousePressedCoordY);
                        } else {
                            MousePressedCoordY = e.getYOnScreen();
                        }
                        DoRotation1(v / 2);
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });

    }
    double angle = 0;

    private void DoRotation1(int ticks) {
        if (ticks != 0) {
            angle += Math.PI * ticks / 40.0;
            repaint();
            DoRotation(ticks);
        }
    }

    public abstract void DoRotation(int ticks);

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