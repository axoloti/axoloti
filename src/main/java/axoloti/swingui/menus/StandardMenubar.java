package axoloti.swingui.menus;

import axoloti.target.TargetController;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

/**
 *
 * @author jtaelman
 */
public class StandardMenubar extends JMenuBar {

    public StandardMenubar() {
        axoloti.swingui.menus.FileMenu fileMenu1 = new axoloti.swingui.menus.FileMenu("File");
        fileMenu1.initComponents();
        add(fileMenu1);

        JMenu boardMenu = new axoloti.swingui.target.TargetMenu(TargetController.getTargetController());
        add(boardMenu);

        JMenu windowMenu1 = new axoloti.swingui.menus.WindowMenu();
        add(windowMenu1);
        JMenu helpMenu1 = new axoloti.swingui.menus.HelpMenu();
        helpMenu1.setText("Help");
        add(helpMenu1);
    }
}
