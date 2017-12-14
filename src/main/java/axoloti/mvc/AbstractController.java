package axoloti.mvc;

import axoloti.property.Property;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

public abstract class AbstractController<Model extends IModel, View extends IView, ParentController extends AbstractController> implements PropertyChangeListener {

    private final ArrayList<View> registeredViews = new ArrayList<View>();
    private final Model model;
    private final ParentController parent;
    private final AbstractDocumentRoot documentRoot;

    public AbstractController(Model model, AbstractDocumentRoot documentRoot, ParentController parent) {
        model.addPropertyChangeListener(this);
        this.model = model;
        this.parent = parent;
        this.documentRoot = documentRoot;
        if (documentRoot == null) {
            //System.out.println("documentroot is null");
        }
    }

    // to be called in controller.createView()
    final public void addView(View view) {
        if (view != null) {
            if (registeredViews.contains(view)) {
                System.out.println("view already added : " + view.toString());
            } else {
                for (Property property : getModel().getProperties()) {
                    Object propertyValue = property.get(getModel());
                    PropertyChangeEvent evt = new PropertyChangeEvent(model, property.getName(), null, propertyValue);
                    view.modelPropertyChange(evt);
                }
                registeredViews.add(view);
            }
        } else {
            System.out.println("view is null");
        }
    }

    final public void removeView(View view) {
        if (!registeredViews.contains(view)) {
// TODO: trace double removal of AJFrame's
//            throw new Error("view was not attached to controller :" + view.toString());
        }
        registeredViews.remove(view);
    }

    public Model getModel() {
        return model;
    }

    public ParentController getParent() {
        return parent;
    }

    public AbstractDocumentRoot getDocumentRoot() {
        return documentRoot;
    }

    //  Use this to observe property changes from registered models
    //  and propagate them on to all the views.
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //System.out.println("propertyChange: " + evt.getPropertyName() + " : " + ((evt.getNewValue()!=null)?evt.getNewValue().toString() : "null"));
        for (View view : registeredViews) {
            view.modelPropertyChange(evt);
        }
    }

    /**
     * This is a convenience method that subclasses can call upon to fire
     * property changes back to the models. This method uses reflection to
     * inspect each of the model classes to determine whether it is the owner of
     * the property in question. If it isn't, a NoSuchMethodException is thrown,
     * which the method ignores.
     *
     * @param property = The property.
     * @param newValue = An object that represents the new value of the
     * property.
     */
    protected void setModelProperty(Property property, Object newValue) {
        property.set(getModel(), newValue);
    }

    public Object getModelProperty(Property property) {
        return property.get(getModel());
    }

    public void addMetaUndo(String actionName) {
        if (documentRoot == null) return;
        documentRoot.getUndoManager().addEdit(new UndoableEditGroup(actionName));
    }

    public void setModelUndoableProperty(Property property, Object newValue) {
        if (getUndoManager() == null) {
            //System.out.println("no undomanager");
            property.set(getModel(),newValue);
        } else {
            Object old_val = property.get(getModel());
            if (old_val == newValue) {
                return;
            }
            property.set(getModel(), newValue);
            UndoableEdit uedit = new UndoablePropertyChange(this, property, old_val, newValue);
            getUndoManager().addEdit(uedit);
            if (documentRoot != null) {
                documentRoot.fireUndoListeners(new UndoableEditEvent(this, uedit));
            }
        }
    }

    public UndoManager getUndoManager() {
        if (documentRoot == null) {
            return null;
        }
        return documentRoot.getUndoManager();
    }

}
