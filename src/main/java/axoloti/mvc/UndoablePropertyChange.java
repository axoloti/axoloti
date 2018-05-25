package axoloti.mvc;

import axoloti.property.Property;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author jtaelman
 */
public class UndoablePropertyChange implements UndoableEdit {

    private Object new_value;
    private Object old_value;
    private IModel model;
    private final Property property;

    public UndoablePropertyChange(IModel model, Property property, Object old_value, Object new_value) {
        this.new_value = new_value;
        this.model = model;
        this.property = property;
        this.old_value = old_value;
        //System.out.println("undoablePropChange: " + propertyName + " : " + ((new_value!=null)?new_value.toString() : "null"));
    }

    public IModel getModel() {
        return model;
    }

    public Property getProperty() {
        return property;
    }

    public Object getOldValue() {
        return old_value;
    }

    public Object getNewValue() {
        return new_value;
    }

    public void setNewValue(Object new_value) {
        this.new_value = new_value;
    }

    @Override
    public void undo() throws CannotUndoException {
        MvcDiagnostics.log(String.format("undo propertyChange %08X %s:%s%n",
                hashCode(),
                getPresentationName(),
                (old_value != null) ? old_value.toString() : "null"));
        model.getController().setModelProperty(property, old_value);
    }

    @Override
    public boolean canUndo() {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException {
        MvcDiagnostics.log(String.format("redo propertyChange %08X %s:%s%n",
                hashCode(),
                getPresentationName(),
                (new_value != null) ? new_value.toString() : "null"));
        model.getController().setModelProperty(property, new_value);
    }

    @Override
    public boolean canRedo() {
        return true;
    }

    @Override
    public void die() {
        new_value = null;
        old_value = null;
        model = null;
    }

    @Override
    public boolean addEdit(UndoableEdit anEdit) {
        if (!(anEdit instanceof UndoablePropertyChange)) {
            return false;
        }
        UndoablePropertyChange anAbsEdit = (UndoablePropertyChange) anEdit;
        if (anAbsEdit.getModel() != model) {
            return false;
        }
        if (!(property == anAbsEdit.getProperty())) {
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
        return false;
    }

    @Override
    public String getPresentationName() {
        return "change " + property.getFriendlyName();
    }

    @Override
    public String getUndoPresentationName() {
        return "Undo change " + property.getFriendlyName();
    }

    @Override
    public String getRedoPresentationName() {
        return "Redo change " + property.getFriendlyName();
    }
}
