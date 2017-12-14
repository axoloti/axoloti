package axoloti.menus;

import axoloti.TargetModel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

/**
 *
 * @author jtaelman
 */
public class StandardMenubar extends JMenuBar {

    public StandardMenubar() {
        axoloti.menus.FileMenu fileMenu1 = new axoloti.menus.FileMenu("File");
        fileMenu1.initComponents();
        add(fileMenu1);

        JMenu boardMenu = new axoloti.menus.TargetMenu(TargetModel.getTargetController());
        add(boardMenu);

        JMenu windowMenu1 = new axoloti.menus.WindowMenu();
        add(windowMenu1);
        JMenu helpMenu1 = new axoloti.menus.HelpMenu();
        helpMenu1.setText("Help");
        add(helpMenu1);
    }
}
