package axoloti.dialogs;

import axoloti.DocumentWindow;
import axoloti.TargetController;
import axoloti.menus.StandardMenubar;
import java.awt.HeadlessException;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author jtaelman
 */
public abstract class TJFrame extends AJFrame<TargetController> {

    private TargetController controller;

    public TJFrame(TargetController controller) throws HeadlessException {
        super(null);
        this.controller = controller;
        setJMenuBar(new StandardMenubar());
    }

    @Override
    public boolean askClose() {
        return false;
    }

    @Override
    public File getFile() {
        return null;
    }

    @Override
    public ArrayList<DocumentWindow> getChildDocuments() {
        return null;
    }

    @Override
    public TargetController getController() {
        return controller;
    }

}
