package axoloti.swingui.target;

import axoloti.abstractui.DocumentWindow;
import axoloti.swingui.menus.StandardMenubar;
import axoloti.swingui.mvc.AJFrame;
import axoloti.target.TargetController;
import axoloti.target.TargetModel;
import java.awt.HeadlessException;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author jtaelman
 */
public abstract class TJFrame extends AJFrame<TargetController> {

    public final StandardMenubar menuBar;

    public TJFrame(TargetController controller) throws HeadlessException {
        super(controller, null);
        menuBar = new StandardMenubar(controller.getDocumentRoot());
        setJMenuBar(menuBar);
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

    public TargetModel getModel() {
        return getController().getModel();
    }

}
