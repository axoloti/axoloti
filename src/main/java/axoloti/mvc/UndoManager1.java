package axoloti.mvc;

import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author jtaelman
 */
public class UndoManager1 extends UndoManager {

    @Override
    public UndoableEdit editToBeUndone() {
        return super.editToBeUndone();
    }
}
