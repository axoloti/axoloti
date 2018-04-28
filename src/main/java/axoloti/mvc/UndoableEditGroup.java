package axoloti.mvc;

import java.util.ArrayList;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author jtaelman
 */
public class UndoableEditGroup implements UndoableEdit {

    final String actionName;
    final ArrayList<UndoablePropertyChange> elements;
    final FocusEdit focusEdit;

    public UndoableEditGroup(String actionName, FocusEdit focusEdit) {
        this.actionName = actionName;
        this.focusEdit = focusEdit;
        elements = new ArrayList<>();
    }

    public UndoableEditGroup(String actionName) {
        this(actionName, null);
    }

    @Override
    public void undo() throws CannotUndoException {
        MvcDiagnostics.log(String.format("undo %08X %s\n", hashCode(), getPresentationName()));
        for (int j = elements.size() - 1; j >= 0; j--) {
            UndoablePropertyChange el = elements.get(j);
            el.undo();
        }
        if (focusEdit != null) {
            focusEdit.focus();
        }
    }

    @Override
    public boolean canUndo() {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException {
        MvcDiagnostics.log(String.format("redo %08X %s\n", hashCode(), getPresentationName()));
        for (UndoablePropertyChange el : elements) {
            el.redo();
        }
        if (focusEdit != null) {
            focusEdit.focus();
        }
    }

    @Override
    public boolean canRedo() {
        return true;
    }

    @Override
    public void die() {
        for (UndoablePropertyChange el : elements) {
            el.die();
        }
    }

    @Override
    public boolean addEdit(UndoableEdit anEdit) {
        if (!(anEdit instanceof UndoablePropertyChange)) {
            return false;
        }
        UndoablePropertyChange anAbsEdit = (UndoablePropertyChange) anEdit;
        if (elements.isEmpty()) {
            elements.add(anAbsEdit);
            return true;
        }
        UndoablePropertyChange lastEdit = elements.get(elements.size() - 1);
        if ((lastEdit.getController() == anAbsEdit.getController())
                && (lastEdit.getProperty() == anAbsEdit.getProperty())) {
            lastEdit.new_value = anAbsEdit.getNewValue();
            if ((lastEdit.new_value != null) && (lastEdit.new_value.equals(lastEdit.old_value))) {
                elements.remove(lastEdit);
            }
            return true;
        } else {
            elements.add(anAbsEdit);
            return true;
        }
    }

    @Override
    public boolean replaceEdit(UndoableEdit anEdit) {
        return false;
    }

    @Override
    public boolean isSignificant() {
        return !elements.isEmpty();
    }

    @Override
    public String getPresentationName() {
        return actionName;
    }

    @Override
    public String getUndoPresentationName() {
        if (elements.size() == 1) {
            return "Undo " + actionName/* + " : " + elements.get(0).getProperty().getFriendlyName()*/;
        }
        return "Undo " + actionName;
    }

    @Override
    public String getRedoPresentationName() {
        if (elements.size() == 1) {
            return "Redo " + actionName/* + " : " + elements.get(0).getProperty().getFriendlyName()*/;
        }
        return "Redo " + actionName;
    }

    public ArrayList<UndoablePropertyChange> getElements() {
        return elements;
    }

}
