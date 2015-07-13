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
package axoloti;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JSeparator;

/**
 *
 * @author Johannes Taelman
 */
public class WindowMenu {

    static class WindowMenuItemActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame frame = ((WindowMenuItem) e.getSource()).getFrame();
            frame.setVisible(true);
            frame.setState(java.awt.Frame.NORMAL);
            frame.toFront();
        }
    }
    static private WindowMenuItemActionListener wmiAL = new WindowMenuItemActionListener();

    static private class WindowMenuItem extends JCheckBoxMenuItem {

        private final JFrame frame;

        public WindowMenuItem(JFrame frame, String itemname) {
            super(frame.getTitle());
            this.frame = frame;
            addActionListener(wmiAL);
        }

        public WindowMenuItem(JFrame frame) {
            super(frame.getTitle());
            this.frame = frame;
            addActionListener(wmiAL);
        }

        public JFrame getFrame() {
            return frame;
        }
    }

    static void PopulateWindowMenu(JMenu jMenuWindow) {
        jMenuWindow.removeAll();
        {
            WindowMenuItem a = new WindowMenuItem(MainFrame.mainframe, "Axoloti");
            //a.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            jMenuWindow.add(a);
        }
        {
            WindowMenuItem a = new WindowMenuItem(MainFrame.mainframe.keyboard, "Keyboard");
            //a.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            jMenuWindow.add(a);
        }
        {
            WindowMenuItem a = new WindowMenuItem(MainFrame.mainframe.filemanager, "File Manager");
            //a.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            jMenuWindow.add(a);
        }
        {
            WindowMenuItem a = new WindowMenuItem(MainFrame.mainframe.remote, "Remote");
            //a.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            jMenuWindow.add(a);
        }

        jMenuWindow.add(new JSeparator());
        for (Patch p : MainFrame.mainframe.patches) {
            JFrame frame = p.patchframe;
            jMenuWindow.add(new WindowMenuItem(frame));
        }
    }
}
