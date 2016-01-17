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
package axoloti.menus;

import axoloti.MainFrame;
import java.util.ArrayList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 *
 * @author Johannes Taelman
 */
public class RecentFileMenu extends JMenu {

    public RecentFileMenu() {

        addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                ArrayList<String> r = MainFrame.prefs.getRecentFiles();
                for (String s : r) {
                    JMenuItem mi = new JMenuItem(s);
                    mi.setActionCommand("open:" + s);
                    mi.addActionListener(MainFrame.mainframe);
                    add(mi, 0);
                }
            }

            @Override
            public void menuDeselected(MenuEvent e) {
                removeAll();
            }

            @Override
            public void menuCanceled(MenuEvent e) {
                removeAll();
            }
        });
    }

}
