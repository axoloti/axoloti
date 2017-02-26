/**
 * Copyright (C) 2013 - 2016 Johannes Taelman
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
import java.io.File;
import java.util.Arrays;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 *
 * @author jtaelman
 */
public class PopulatePatchMenuDynamic extends JMenu implements MenuListener {

    final String ext;
    final File dir;

    PopulatePatchMenuDynamic(String name, File dir, String ext) {
        super(name);
        this.ext = ext;
        this.dir = dir;
        addMenuListener(this);
    }

    @Override
    public void menuSelected(MenuEvent e) {
        boolean bEmpty = true;
        if (dir.exists() && dir.isDirectory()) {
            for (File subdir : PopulatePatchMenuDynamic.this.dir.listFiles(new java.io.FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory() && !pathname.isHidden();
                }
            })) {
                PopulatePatchMenuDynamic fm = new PopulatePatchMenuDynamic(subdir.getName(), subdir, PopulatePatchMenuDynamic.this.ext);
                add(fm);
                bEmpty = false;
            }
            File[] files = PopulatePatchMenuDynamic.this.dir.listFiles(new java.io.FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isFile() && f.canRead() && f.getName().endsWith(ext);
                }
            });
            Arrays.sort(files);
            for (File f : files) {
                String fn = f.getName();
                String fn2 = fn.substring(0, fn.length() - 4);
                JMenuItem fm = new JMenuItem(fn2);
                String a = "open:" + dir.getPath() + File.separator + fn;
                fm.setActionCommand(a);
                fm.addActionListener(MainFrame.mainframe);
                add(fm);
                bEmpty = false;
            }
        }
        if (bEmpty) {
            JMenuItem m = new JMenuItem("no patches here");
            m.setEnabled(false);
            add(m);
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
}
