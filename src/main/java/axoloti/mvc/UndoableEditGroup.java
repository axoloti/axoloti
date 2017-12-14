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

    public UndoableEditGroup(String actionName) {
        this.actionName = actionName;
        elements = new ArrayList<>();
    }

    @Override
    public void undo() throws CannotUndoException {
        for (int j = elements.size() - 1; j >= 0; j--) {
            UndoablePropertyChange el = elements.get(j);
            el.undo();
        }
    }

    @Override
    public boolean canUndo() {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException {
        for (UndoablePropertyChange el : elements) {
            el.redo();
        }
    }

    @Override
    public boolean canRedo() {
        return true;
    }

    @Override
    public void die() {
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
        if (elements.size() == 1)
            return "Undo " + actionName + " : " + elements.get(0).getProperty().getFriendlyName();
        return "Undo " + actionName;
    }

    @Override
    public String getRedoPresentationName() {
        if (elements.size() == 1)
            return "Redo " + actionName + " : " + elements.get(0).getProperty().getFriendlyName();
        return "Redo " + actionName;
    }

}
