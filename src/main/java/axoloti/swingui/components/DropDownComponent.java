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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author Johannes Taelman
 */
public class DropDownComponent extends JComponent {

    public interface DDCListener {

        void selectionChanged();
    }

    private int selectedIndex;
    private List<String> items;

    public DropDownComponent(List<String> items) {
        selectedIndex = 0;
        this.items = items;
        initComponent(items);
    }

    private void initComponent(List<String> items) {
        setItems(items);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                doPopup();
            }
        });
    }

    public void setItems(List<String> items) {
        this.items = items;
        FontRenderContext frc = new FontRenderContext(null, true, true);
        int maxWidth = 0;
        for (String s : items) {
            TextLayout tl = new TextLayout(s, Constants.FONT, frc);
            Rectangle2D r = tl.getBounds();
            if (maxWidth < r.getWidth()) {
                maxWidth = (int) r.getWidth();
            }
        }
        Dimension d = new Dimension(maxWidth + 10, 15);
        setSize(d);
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(new Dimension(5000, 15));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        if (isEnabled()) {
            g2.setPaint(Theme.getCurrentTheme().Component_Secondary);
        } else {
            g2.setPaint(Theme.getCurrentTheme().Object_Default_Background);
        }
        g2.fillRect(1, 1, getWidth() - 2, getHeight() - 2);
        g2.setColor(Theme.getCurrentTheme().Component_Primary);
        g2.drawRect(1, 1, getWidth() - 2, getHeight() - 2);
        g2.setColor(Theme.getCurrentTheme().Component_Primary);
        final int rmargin = 5;
        final int htick = 3;
        int[] xp = new int[]{getWidth() - rmargin - htick * 2, getWidth() - rmargin, getWidth() - rmargin - htick};
        final int vmargin = 5;
        int[] yp = new int[]{vmargin, vmargin, vmargin + htick * 2};
        g2.fillPolygon(xp, yp, 3);
        setFont(Constants.FONT);
        if (items.size() > 0) {
            g2.drawString(items.get(selectedIndex), 4, 12);
        }
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedItem(String selection) {
        int index = items.indexOf(selection);
        if ((selectedIndex != index) && (index >= 0)) {
            selectedIndex = index;
            //ItemEvent ie = new ItemEvent(this, 0, items.get(SelectedIndex), 0);
            repaint();
        }
    }

    public String getSelectedItem() {
        return items.get(selectedIndex);
    }

    public int getItemCount() {
        return items.size();
    }

    public String getItemAt(int i) {
        return items.get(i);
    }

    private final List<DDCListener> ddcListeners = new LinkedList<>();

    void doPopup() {
        if (isEnabled()) {
            JPopupMenu p = new JPopupMenu();
            for (String s : items) {
                JMenuItem mi = p.add(s);
                mi.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setSelectedItem(e.getActionCommand());
                        for (DDCListener il : ddcListeners) {
                            il.selectionChanged();
                        }
                    }
                });
            }
            this.add(p);
            p.show(this, 0, getHeight() - 1);
        }
    }

    public void addItemListener(DDCListener itemListener) {
        ddcListeners.add(itemListener);
    }

}
