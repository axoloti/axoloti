package axoloti.mvc;

import axoloti.property.BooleanProperty;
import axoloti.property.ObjectProperty;
import axoloti.property.ObjectROProperty;
import axoloti.property.Property;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author jtaelman
 */
public class AbstractDocumentRoot extends AbstractModel<DocumentRootController> {

    private final UndoManager1 undoManager = new UndoManager1(this);
    private UndoableEdit lastUndoableEditEventWhenSaved = null;

    private boolean dirty;
    private UndoableEdit editToBeUndone;

    public UndoManager1 getUndoManager() {
        return undoManager;
    }

    @Override
    public AbstractDocumentRoot getDocumentRoot() {
        return this;
    }

    private final List<UndoableEditListener> undoListeners = new LinkedList<>();

    public void addUndoListener(UndoableEditListener uel) {
        undoListeners.add(uel);
    }

    void fireUndoListeners(UndoableEditEvent e) {
        for (UndoableEditListener uel : undoListeners) {
            uel.undoableEditHappened(e);
        }
        firePropertyChange(UNDO_EVENTS, null, getUndoEvents());
        if (undoManager.editToBeUndone() == null) {
            setDirty(false);
        }
        setDirty(!(lastUndoableEditEventWhenSaved == undoManager.editToBeUndone()));
    }

    /*
     * return true if the user needs to be asked to save the document before closing
     */
    public Boolean getDirty() {
        return dirty;
    }

    public void setDirty(Boolean dirty) {
        if (dirty == false) {
            lastUndoableEditEventWhenSaved = undoManager.editToBeUndone();
        }
        boolean prev = this.dirty;
        this.dirty = dirty;
        firePropertyChange(DIRTY, prev, dirty);
    }

    public List getUndoEvents() {
        return getUndoManager().getEdits();
    }

    public void setEditToBeUndone(UndoableEdit i) {
        editToBeUndone = i;
        firePropertyChange(EDIT_TO_BE_UNDONE, null, editToBeUndone);
    }

    public UndoableEdit getEditToBeUndone() {
        return editToBeUndone;
    }

    public void markSaved() {
        // System.out.println("markSaved");
        setDirty(false);
    }

    public static final Property DIRTY = new BooleanProperty("Dirty", AbstractDocumentRoot.class);
    public static final Property UNDO_EVENTS = new ObjectROProperty("UndoEvents", List.class, AbstractDocumentRoot.class);
    public static final Property EDIT_TO_BE_UNDONE = new ObjectProperty("EditToBeUndone", UndoableEdit.class, AbstractDocumentRoot.class);

    @Override
    public List<Property> getProperties() {
        ArrayList<Property> l = new ArrayList<>();
        l.add(DIRTY);
        l.add(UNDO_EVENTS);
        return l;
    }

    @Override
    public IModel getParent() {
        return null;
    }

    @Override
    protected DocumentRootController createController() {
        return new DocumentRootController(this);
    }

}
