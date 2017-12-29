package axoloti.mvc;

import java.util.ArrayList;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author jtaelman
 */
public class AbstractDocumentRoot {

    private final UndoManager1 undoManager = new UndoManager1();
    private UndoableEdit lastUndoableEditEventWhenSaved = null;

    public UndoManager getUndoManager() {
        return undoManager;
    }

    private ArrayList<UndoableEditListener> undoListeners = new ArrayList<>();

    public void addUndoListener(UndoableEditListener uel) {
        undoListeners.add(uel);
    }

    void fireUndoListeners(UndoableEditEvent e) {
        for (UndoableEditListener uel : undoListeners) {
            uel.undoableEditHappened(e);
        }
    }

    /*
    * return true if the user needs to be asked to save the document before closing
     */
    public boolean getDirty() {
        if (undoManager.editToBeUndone() == null) {
            return false;
        }
        return !(lastUndoableEditEventWhenSaved == undoManager.editToBeUndone());
    }

    public void markSaved() {
        // System.out.println("markSaved");
        lastUndoableEditEventWhenSaved = undoManager.editToBeUndone();
    }

}
