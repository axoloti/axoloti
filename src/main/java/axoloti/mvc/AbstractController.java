package axoloti.mvc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

public abstract class AbstractController<Model extends IModel, View extends IView, ParentController extends AbstractController> implements PropertyChangeListener {

    final private ArrayList<View> registeredViews = new ArrayList<View>();
    private final Model model;
    private final ParentController parent;
    final AbstractDocumentRoot documentRoot;

    public AbstractController(Model model, AbstractDocumentRoot documentRoot, ParentController parent) {
        model.addPropertyChangeListener(this);
        this.model = model;
        this.parent = parent;
        this.documentRoot = documentRoot;
        if (documentRoot == null) {
            //System.out.println("documentroot is null");
        }
    }

    public String[] getPropertyNames() {
        return new String[]{};
    }

    // to be called in controller.createView()
    final public void addView(View view) {
        if (view != null) {
            if (registeredViews.contains(view)) {
                System.out.println("view already added : " + view.toString());
            } else {
                for (String propertyName : getPropertyNames()) {
                    Object propertyValue = getModelProperty(propertyName);
                    PropertyChangeEvent evt = new PropertyChangeEvent(model, propertyName, null, propertyValue);
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
            throw new Error("view was not attached to controller" + view.toString());
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
     * @param propertyName = The name of the property.
     * @param newValue = An object that represents the new value of the
     * property.
     */
    protected void setModelProperty(String propertyName, Object newValue) {

        try {

            Method method = model.getClass().
                    getMethod("set" + propertyName, new Class[]{
                        newValue.getClass()
                    }
                    );
            method.invoke(model, newValue);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(AbstractController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(AbstractController.class.getName()).log(Level.SEVERE, null, ex.getCause());
//                System.out.println(ex.getMessage() + " " + ex.getCause().toString());
        } catch (IllegalAccessException ex) {
            Logger.getLogger(AbstractController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(AbstractController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Object getModelProperty(String propertyName) {
        try {

            Method method = model.getClass().
                    getMethod("get" + propertyName, new Class[]{});
            Object val = method.invoke(model);
            return val;

        } catch (NoSuchMethodException ex) {
            Logger.getLogger(AbstractController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(AbstractController.class.getName()).log(Level.SEVERE, null, ex.getCause());
//                System.out.println(ex.getMessage() + " " + ex.getCause().toString());
        } catch (IllegalAccessException ex) {
            Logger.getLogger(AbstractController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(AbstractController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void addMetaUndo(String actionName) {
        if (documentRoot == null) return;
        documentRoot.getUndoManager().addEdit(new UndoableEditGroup(actionName));
    }
    
    public void setModelUndoableProperty(String propertyName, Object newValue) {
        if (getUndoManager() == null) {
            //System.out.println("no undomanager");
            setModelProperty(propertyName, newValue);
        } else {
            Object old_val = getModelProperty(propertyName);
            if (old_val == null) {
                System.out.println("Property method get" + propertyName + " returned null, this is illegal for undoable property changes.");
                throw new Error("Property method get" + propertyName + " returned null, this is illegal for undoable property changes.");
            }            
            if (old_val == newValue) {
                return;
            }
            setModelProperty(propertyName, newValue);
            UndoableEdit uedit = new UndoablePropertyChange(this, propertyName, old_val, newValue);
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
