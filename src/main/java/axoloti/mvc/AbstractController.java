package axoloti.mvc;

import axoloti.patch.net.NetDrag;
import axoloti.property.ListProperty;
import axoloti.property.Property;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

public abstract class AbstractController<Model extends IModel, View extends IView> implements PropertyChangeListener {

    private final List<View> registeredViews = new LinkedList<>();
    protected final Model model;

    protected AbstractController(Model model) {
        if ((model.getDocumentRoot() == null) && (model.getParent() != null)) {
            model.setDocumentRoot(model.getParent().getDocumentRoot());
        }
        this.model = model;
        init();
    }

    private void init() {
        model.addPropertyChangeListener(this);
    }

    // to be called in controller.createView()
    final public void addView(View view) {
        if (view != null) {
            if (registeredViews.contains(view)) {
                System.out.println("view already added : " + view.toString());
//                throw new Error("view already added");
            } else {
                for (Property property : model.getProperties()) {
                    Object propertyValue = property.get(model);
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
            String s = "view was not attached to controller :" + view.toString();
            System.out.println(s);
//            throw new Error(s);
        }
        registeredViews.remove(view);
    }

    protected Model getModel() {
        return model;
    }

    public AbstractDocumentRoot getDocumentRoot() {
        if (model == null) {
            throw new Error("model is null");
        }
        if (model.getDocumentRoot() == null) {
            MvcDiagnostics.log("documentroot is null, " + this.toString());
        }
        return model.getDocumentRoot();
    }

    //  Use this to observe property changes from registered models
    //  and propagate them on to all the views.
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //System.out.println("propertyChange: " + evt.getPropertyName() + " : " + ((evt.getNewValue()!=null)?evt.getNewValue().toString() : "null"));

        // this check fails with automated tests
        if (!SwingUtilities.isEventDispatchThread()) {
//            Logger.getLogger(AbstractController.class.getName()).log(Level.SEVERE, "not in EventDispatchThread");
        }
        // a copy of the views is made
        // so a view can unregister itself in response to a propertychange
        List<View> v = new LinkedList<>(registeredViews);
        for (View view : v) {
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
        property.set(model, newValue);
    }

    public Object getModelProperty(Property property) {
        return property.get(model);
    }

    public void addMetaUndo(String actionName, FocusEdit focusEdit) {
        UndoManager undoManager = getUndoManager();
        if (undoManager == null) {
            System.out.println("addMetaUndo: no undomanager: " + actionName);
            return;
        }
        undoManager.addEdit(new UndoableEditGroup(actionName, focusEdit));
    }

    public void addMetaUndo(String actionName) {
        // TODO: add FocusEdit where possible
        addMetaUndo(actionName, null);
    }

    protected void setModelUndoableProperty(Property property, Object newValue) {
        UndoManager undoManager = getUndoManager();
        if (undoManager == null) {
            if (!(model instanceof NetDrag)) {
                MvcDiagnostics.log("setModelUndoableProperty: no undomanager: " + model.getClass().toString());
            }
            property.set(model, newValue);
        } else {
            Object old_val = property.get(model);
            if (old_val == newValue) {
                return;
            }
            if ((newValue != null) && (newValue.equals(old_val))) {
                return;
            }
            UndoableEdit uedit = new UndoablePropertyChange(getModel(), property, old_val, newValue);
            MvcDiagnostics.log("setModelUndoableProperty property " + ("" + undoManager.isInProgress() + " ") + property.getFriendlyName() + " old:" + old_val + " new:" + newValue + "\n");
            undoManager.addEdit(uedit);
            if (getDocumentRoot() != null) {
                getDocumentRoot().fireUndoListeners(new UndoableEditEvent(this, uedit));
            } else {
                System.out.println("DocumentRoot null?");
            }
            property.set(model, newValue);
        }
    }

    public void generic_setModelUndoableProperty(Property property, Object newValue) {
        // use setModelUndoableProperty where possible in Controller classes
        setModelUndoableProperty(property, newValue);
    }

    protected void addUndoableElementToList(ListProperty property, Object newItem) {
        boolean WarnIfParentIsNotArrayContainer = false;
        if (WarnIfParentIsNotArrayContainer) {
            // actually this is not aa valid assumption for Net source/dest,
            // whose parents are ObjectInstances...
            if (newItem instanceof IModel) {
                IModel m = (IModel) newItem;
                if (m.getParent() != model) {
                    System.out.println("error: model parent is wrong, should be " + model.toString() + " ???");
                }
            }
        }
        List old = (List) getModelProperty(property);
        if (old.contains(newItem)) {
            System.out.println("error: list property already contains element");
            return;
        }
        List list = new ArrayList(old.size() + 1);
        list.addAll(old);
        list.add(newItem);
        setModelUndoableProperty(property, list);
    }

    public void generic_addUndoableElementToList(ListProperty property, Object newItem) {
        // use addUndoableElementToList instead (in Controller classes)
        addUndoableElementToList(property, newItem);
    }

    protected boolean removeUndoableElementFromList(ListProperty property, Object item) {
        List old = (List) getModelProperty(property);
        List list = new ArrayList(old);
        boolean success = list.remove(item);
        if (!success) {
            return false;
        }
        setModelUndoableProperty(property, list);
        return true;
    }

    public boolean generic_removeUndoableElementFromList(ListProperty property, Object item) {
        return removeUndoableElementFromList(property, item);
    }

    public UndoManager1 getUndoManager() {
        AbstractDocumentRoot dr = getDocumentRoot();
        if (dr == null) {
            return null;
        }
//        System.out.println("                dr:" + dr.getUndoManager().hashCode());
        return dr.getUndoManager();
    }

}
