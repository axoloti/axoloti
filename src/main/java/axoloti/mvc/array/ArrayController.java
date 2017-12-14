package axoloti.mvc.array;

import axoloti.NetController;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.IModel;
import axoloti.property.Property;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author jtaelman
 */
public abstract class ArrayController<T extends AbstractController, M extends IModel, P extends AbstractController> implements Iterable<T> {

    final ArrayList<T> subcontrollers;

    public abstract T createController(M model, AbstractDocumentRoot documentRoot, P parent);
    public abstract void disposeController(T controller);

    final P parent;
    final Property property;
    
    public ArrayController(P parent, Property property) {
        this.parent = parent;
        this.property = property;
        subcontrollers = new ArrayList<>();
        syncControllers();
    }

    public void add(M m) {
        ArrayList<M> n = new ArrayList<>(((List<M>) parent.getModelProperty(property)));
        if (n.contains(m)) {
            System.out.println("array already contains model :" + m.toString());
        }
        n.add(m);
        parent.setModelUndoableProperty(property, n);
    }

    public boolean remove(M m) {
        ArrayList<M> n = new ArrayList<>(((List<M>) parent.getModelProperty(property)));
        boolean r = n.remove(m);
        if (r) {
            parent.setModelUndoableProperty(property, n);
        } else {
            throw new Error("model did not contain " + m);
        }
        return r;
    }

    public final void syncControllers() {
        ArrayList<T> subcontrollers2 = (ArrayList<T>) subcontrollers.clone();
        List<M> models = (List<M>) parent.getModelProperty(property);
        for (T c: subcontrollers2) {
            M m = (M)c.getModel();
            if (!models.contains(m)) {
                disposeController(c);
            }
        }
        subcontrollers.clear();
        ArrayList<M> models_clone = new ArrayList<>(models);
        for (M om : models_clone) {
            AbstractController ctrl = null;
            for (AbstractController ctrl2 : subcontrollers2) {
                if (om == ctrl2.getModel()) {
                    ctrl = ctrl2;
                    break;
                }
            }
            if (ctrl == null) {
                ctrl = createController(om, parent.getDocumentRoot(), parent);
            }
            subcontrollers.add((T) ctrl);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (property.is(evt)) {
            // TODO : change to comparing properties
            syncControllers();
        }
    }

    @Override
    public Iterator<T> iterator() {
        return subcontrollers.iterator();
    }

    public T get(int index) {
        return subcontrollers.get(index);
    }

    public void moveDown(int row) {
        if (row < 0) {
            return;
        }
        ArrayList<M> n = new ArrayList<>(((List<M>) parent.getModelProperty(property)));
        if (row > (n.size() - 1)) {
            return;
        }
        M o = n.remove(row);
        n.add(row + 1, o);
        parent.setModelUndoableProperty(property, n.toArray());
    }

    public int indexOf(NetController netController) {
        int index = subcontrollers.indexOf(netController);
        if (index < 0) {
            throw new Error("ArrayController does not contain " + netController.toString());
        }
        return index;
    }

}
