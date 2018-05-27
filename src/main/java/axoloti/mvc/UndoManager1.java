package axoloti.mvc;

import java.util.List;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author jtaelman
 */
public class UndoManager1 extends UndoManager {

    final AbstractDocumentRoot documentRoot;

    public UndoManager1(AbstractDocumentRoot documentRoot) {
        this.documentRoot = documentRoot;
    }

    @Override
    public UndoableEdit editToBeUndone() {
        return super.editToBeUndone();
    }

    public List<UndoableEdit> getEdits() {
        return edits;
    }

    private boolean undoing = false;
    private boolean redoing = false;


    @Override
    public synchronized boolean addEdit(UndoableEdit anEdit) {
        if (redoing) {
            MvcDiagnostics.log("undo addEdit: not registering since we're redoing\n");
            return false;
        }
        if (undoing) {
            MvcDiagnostics.log("undo addEdit: warning! registering edit while undoing?\n");
            return false;
        }
        MvcDiagnostics.log("addEdit: " + String.format("%08X ", anEdit.hashCode()) + anEdit.getPresentationName() + "\n");
        boolean r = super.addEdit(anEdit);
        documentRoot.setEditToBeUndone(editToBeUndone());
        return r;
    }

    @Override
    public synchronized void undo() throws CannotUndoException {
        MvcDiagnostics.log("<<<< undo\n");
        if (!canUndo()) {
            MvcDiagnostics.log(">>>> undo\n");
            return;
        }
        undoing = true;
        super.undo();
        undoing = false;
        MvcDiagnostics.log(">>>> undo\n");
        documentRoot.setEditToBeUndone(editToBeUndone());
    }

    @Override
    public synchronized void redo() throws CannotRedoException {
        MvcDiagnostics.log("<<<< redo\n");
        if (!canRedo()) {
            MvcDiagnostics.log(">>>> redo\n");
            return;
        }
        redoing = true;
        super.redo();
        redoing = false;
        MvcDiagnostics.log(">>>> redo\n");
        documentRoot.setEditToBeUndone(editToBeUndone());
    }

    public void printEdits() {
        MvcDiagnostics.log("undo edits:\n");
        for (UndoableEdit e : getEdits()) {
            if (e.isSignificant()) {
                MvcDiagnostics.log("[ ]  ");
            } else {
                MvcDiagnostics.log("[S]  ");
            }
            System.out.println(e.getPresentationName());
            if (e instanceof UndoableEditGroup) {
                UndoableEditGroup ug = (UndoableEditGroup) e;
                for (UndoableEdit ge : ug.elements) {
                    System.out.println("  - " + ge.getPresentationName());
                }
            }
        }
    }

}
