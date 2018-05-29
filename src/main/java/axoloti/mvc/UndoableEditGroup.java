package axoloti.mvc;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author jtaelman
 */
public class UndoableEditGroup implements UndoableEdit {

    final String actionName;
    final List<UndoableEdit> elements;
    final FocusEdit focusEdit;

    public UndoableEditGroup(String actionName, FocusEdit focusEdit) {
        this.actionName = actionName;
        this.focusEdit = focusEdit;
        elements = new LinkedList<>();
    }

    public UndoableEditGroup(String actionName) {
        this(actionName, null);
    }

    @Override
    public void undo() throws CannotUndoException {
        MvcDiagnostics.log(String.format("undo %08X %s%n", hashCode(), getPresentationName()));
        for (int j = elements.size() - 1; j >= 0; j--) {
            UndoableEdit el = elements.get(j);
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
        MvcDiagnostics.log(String.format("redo %08X %s%n", hashCode(), getPresentationName()));
        for (UndoableEdit el : elements) {
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
        for (UndoableEdit el : elements) {
            el.die();
        }
    }

    @Override
    public boolean addEdit(UndoableEdit anEdit) {

        if (anEdit instanceof UndoableEditGroup) {
            return false;
        }

        if (elements.isEmpty()) {
            elements.add(anEdit);
            return true;
        }

        UndoableEdit lastEdit = elements.get(elements.size() - 1);

        if ((lastEdit instanceof UndoablePropertyChange) && (anEdit instanceof UndoablePropertyChange)) {
            UndoablePropertyChange lastEditPC = (UndoablePropertyChange) lastEdit;
            UndoablePropertyChange anEditPC = (UndoablePropertyChange) anEdit;
            if ((lastEditPC.getModel() == anEditPC.getModel())
                    && (lastEditPC.getProperty() == anEditPC.getProperty())) {
                lastEditPC.setNewValue(anEditPC.getNewValue());
                if ((lastEditPC.getNewValue() != null) && (lastEditPC.getNewValue().equals(lastEditPC.getOldValue()))) {
                    elements.remove(lastEdit);
                }
                return true;
            } else {
                elements.add(anEdit);
                return true;
            }

        } else {
            elements.add(anEdit);
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

    public List<UndoableEdit> getElements() {
        return Collections.unmodifiableList(elements);
    }

    public boolean canGrabFocus() {
        return (focusEdit != null);
    }

    public void grabFocus() {
        if (focusEdit != null) {
            focusEdit.focus();
        }
    }

}
