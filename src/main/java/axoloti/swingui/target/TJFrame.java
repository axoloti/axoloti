package axoloti.swingui.target;

import axoloti.abstractui.DocumentWindow;
import axoloti.swingui.menus.StandardMenubar;
import axoloti.swingui.mvc.AJFrame;
import axoloti.target.TargetModel;
import java.awt.HeadlessException;
import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author jtaelman
 */
public abstract class TJFrame extends AJFrame<TargetModel> {

    private StandardMenubar menuBar;

    public TJFrame(TargetModel targetModel) throws HeadlessException {
        super(targetModel, null);
        initComponents2();
    }

    private void initComponents2() {
        menuBar = new StandardMenubar(model.getController().getDocumentRoot());
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
    public List<DocumentWindow> getChildDocuments() {
        return Collections.emptyList();
    }

}
