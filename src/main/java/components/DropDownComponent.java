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
import axoloti.attribute.AttributeInstanceComboBox;
import axoloti.utils.Constants;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author Johannes Taelman
 */
public class DropDownComponent extends JComponent implements MouseListener {
    
    public interface DDCListener {
        
        public void SelectionChanged();
    }
    
    int SelectedIndex;
    ArrayList<String> Items;
    
    final private AttributeInstanceComboBox parent;
    
    public DropDownComponent(ArrayList<String> Items, AttributeInstanceComboBox parent) {
        this.Items = Items;
        this.parent = parent;
        SelectedIndex = 0;
        
        FontRenderContext frc = new FontRenderContext(null, true, true);
        int maxWidth = 0;
        for (String s : Items) {
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
        
        addMouseListener((MouseListener) this);
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
        if (Items.size() > 0) {
            g2.drawString(Items.get(SelectedIndex), 4, 12);
        }
    }
    
    public int getSelectedIndex() {
        return SelectedIndex;
    }
    
    public void setSelectedItem(String selection) {
        int index = Items.indexOf(selection);
        if ((SelectedIndex != index) && (index >= 0)) {
            SelectedIndex = index;
            //ItemEvent ie = new ItemEvent(this, 0, Items.get(SelectedIndex), 0);
            for (DDCListener il : ddcListeners) {
                il.SelectionChanged();
            }
            repaint();
        }
    }
    
    public String getSelectedItem() {
        return Items.get(SelectedIndex);
    }
    
    public int getItemCount() {
        return Items.size();
    }
    
    public String getItemAt(int i) {
        return Items.get(i);
    }
    
    ArrayList<DDCListener> ddcListeners = new ArrayList<DDCListener>();
    
    void doPopup() {
        if (isEnabled()) {
            JPopupMenu p = new JPopupMenu();
            for (String s : Items) {
                JMenuItem mi = p.add(s);
                mi.addActionListener(new ActionListener() {
                    
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setSelectedItem(e.getActionCommand());
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
    
    @Override
    public void mouseClicked(MouseEvent e) {
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        doPopup();
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
    }
    
}
