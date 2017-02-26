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
import java.io.FilenameFilter;
import java.util.Arrays;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *
 * @author jtaelman
 */
public class PopulatePatchMenu {

    static void PopulatePatchMenu(JMenu parent, String path, String ext) {
        File dir = new File(path);
        if (!dir.isDirectory()) {
            JMenuItem mi = new JMenuItem("no help patches found");
            mi.setEnabled(false);
            parent.add(mi);
            return;
        }
        final String extension = ext;
        File[] files = dir.listFiles(new java.io.FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        Arrays.sort(files);
        for (File subdir : files) {
            JMenu fm = new JMenu(subdir.getName());
            PopulatePatchMenu(fm, subdir.getPath(), extension);
            if (fm.getItemCount() > 0) {
                parent.add(fm);
            }
        }

        String filenames[] = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (name.endsWith(extension));
            }
        });

        Arrays.sort(filenames);

        for (String fn : filenames) {
            String fn2 = fn.substring(0, fn.length() - 4);
            JMenuItem fm = new JMenuItem(fn2);
            fm.setActionCommand("open:" + path + File.separator + fn);
            fm.addActionListener(MainFrame.mainframe);
            parent.add(fm);
        }
    }
}
