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
package axoloti.swingui.menus;

import axoloti.abstractui.DocumentWindow;
import axoloti.abstractui.DocumentWindowList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

/**
 *
 * @author Johannes Taelman
 */
public class WindowMenu extends JMenu {

    public WindowMenu() {
        initComponents();
    }

    private void initComponents() {
        setText("Window");
        addMenuListener(new javax.swing.event.MenuListener() {
            @Override
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }

            @Override
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
                jMenuWindowMenuDeselected(evt);
            }

            @Override
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                jMenuWindowMenuSelected(evt);
            }
        });
    }

    private void jMenuWindowMenuSelected(javax.swing.event.MenuEvent evt) {
        populateWindowMenu(this);
    }

    private void jMenuWindowMenuDeselected(javax.swing.event.MenuEvent evt) {
        removeAll();
    }

    static class WindowMenuItemActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ((WindowMenuItem) e.getSource()).getDocumentWindow().toFront();
        }
    }
    static private WindowMenuItemActionListener wmiAL = new WindowMenuItemActionListener();

    static private class WindowMenuItem extends JCheckBoxMenuItem {

        final private DocumentWindow documentWindow;

        public WindowMenuItem(DocumentWindow documentWindow, String itemname) {
            super(itemname);
            this.documentWindow = documentWindow;
            WindowMenuItem.this.addActionListener(wmiAL);
        }

        public WindowMenuItem(DocumentWindow documentWindow) {
            super(documentWindow.getTitle());
            this.documentWindow = documentWindow;
            WindowMenuItem.this.addActionListener(wmiAL);
        }

        public DocumentWindow getDocumentWindow() {
            return documentWindow;
        }
    }

    static void populateDocuments(JMenu jMenuWindow, String prefix, List<DocumentWindow> dwl) {
        for (DocumentWindow p : dwl) {
            WindowMenuItem wmi = new WindowMenuItem(p, prefix + p.getTitle());
            jMenuWindow.add(wmi);
            if (p.getChildDocuments() != null) {
                populateDocuments(jMenuWindow, "> " + prefix, p.getChildDocuments());
            }
        }
    }

    static void populateWindowMenu(JMenu jMenuWindow) {
        jMenuWindow.removeAll();
        populateDocuments(jMenuWindow, "", DocumentWindowList.getList());
    }
}
