package axoloti.mvc;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author jtaelman
 */
public class UndoablePropertyChange implements UndoableEdit {

    Object new_value;
    final Object old_value;
    final AbstractController controller;
    final String propertyName;

    public UndoablePropertyChange(AbstractController controller, String propertyName, Object old_value, Object new_value) {
        this.new_value = new_value;
        this.controller = controller;
        this.propertyName = propertyName;
        this.old_value = old_value;
        //System.out.println("undoablePropChange: " + propertyName + " : " + ((new_value!=null)?new_value.toString() : "null"));
    }

    public AbstractController getController() {
        return controller;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Object getNewValue() {
        return new_value;
    }

    @Override
    public void undo() throws CannotUndoException {
        controller.setModelProperty(propertyName, old_value);
    }

    @Override
    public boolean canUndo() {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException {
        controller.setModelProperty(propertyName, new_value);
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
        if (anAbsEdit.getController() != controller) {
            return false;
        }
        if (!propertyName.equals(anAbsEdit.getPropertyName())) {
            return false;
        }
        new_value = anAbsEdit.getNewValue();
        return true;
    }

    @Override
    public boolean replaceEdit(UndoableEdit anEdit) {
        return false;
    }

    @Override
    public boolean isSignificant() {
        return true;
    }

    @Override
    public String getPresentationName() {
        return "change";
    }

    @Override
    public String getUndoPresentationName() {
        return "Undo change " + propertyName;
    }

    @Override
    public String getRedoPresentationName() {
        return "Redo change " + propertyName;
    }
}
