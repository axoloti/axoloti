package axoloti.menus;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

/**
 *
 * @author jtaelman
 */
public class StandardMenubar extends JMenuBar {

    public StandardMenubar() {
        axoloti.menus.FileMenu fileMenu1 = new axoloti.menus.FileMenu("File");
        add(fileMenu1);
        JMenu windowMenu1 = new axoloti.menus.WindowMenu();
        add(windowMenu1);
        JMenu helpMenu1 = new axoloti.menus.HelpMenu();
        helpMenu1.setText("Help");
        add(helpMenu1);
        fileMenu1.initComponents();
    }
}
