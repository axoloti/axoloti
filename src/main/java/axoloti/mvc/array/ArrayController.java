package axoloti.mvc.array;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.AbstractModel;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author jtaelman
 */
public abstract class ArrayController<T extends AbstractController, M extends AbstractModel, P extends AbstractController> extends AbstractController<ArrayModel, ArrayView, P> implements Iterable<T> {

    static final String ARRAY = "Array";

    ArrayList<T> subcontrollers = new ArrayList<>();

    public abstract T createController(M model, AbstractDocumentRoot documentRoot, P parent);

    public ArrayController(ArrayModel model, AbstractDocumentRoot documentRoot, P parent) {
        super(model, documentRoot, parent);
        ArrayList<M> am = model.getArray();
        for (M m : am) {
            subcontrollers.add(createController(m, documentRoot, parent));
        }
    }

    public T add(M m) {
        AbstractController c = createController(m, getDocumentRoot(), getParent());
        subcontrollers.add((T) c);
        ArrayList<M> n = (ArrayList<M>) (getModel().getArray().clone());
        n.add(m);
        setModelUndoableProperty(ARRAY, n);
        return (T) c;
    }

    public boolean remove(M m) {
        ArrayList<AbstractModel> n = (ArrayList<AbstractModel>) (getModel().getArray().clone());
        boolean r = n.remove(m);
        if (r) {
            setModelUndoableProperty(ARRAY, n);
        }
        return r;
    }

    public void syncControllers() {
        ArrayList<AbstractController> subcontrollers2 = (ArrayList<AbstractController>) subcontrollers.clone();
        subcontrollers.clear();
        for (Object o : getModel().array) {
            M om = (M) o;
            AbstractController ctrl = null;
            for (AbstractController ctrl2 : subcontrollers2) {
                if (om == ctrl2.getModel()) {
                    ctrl = ctrl2;
                    break;
                }
            }
            if (ctrl == null) {
                ctrl = createController(om, getDocumentRoot(), getParent());
            }
            subcontrollers.add((T) ctrl);
        }
    }

    /*
     */
    @Override
    public ArrayModel getModel() {
        return super.getModel();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        syncControllers();
        super.propertyChange(evt);
    }

    @Override
    public Iterator<T> iterator() {
        return subcontrollers.iterator();
    }

    public T get(int index) {
        return subcontrollers.get(index);
    }

    public void moveUp(int index) {
        if (index < 1) {
            return;
        }
        ArrayList<M> n = (ArrayList<M>) (getModel().getArray().clone());
        M elem = n.get(index);
        n.remove(index);
        n.add(index - 1, elem);
        setModelUndoableProperty(ARRAY, n);
    }

    public void moveDown(int row) {
        if (row < 0) {
            return;
        }
        if (row > (getModel().getArray().size() - 1)) {
            return;
        }
        ArrayList<M> n = (ArrayList<M>) (getModel().getArray().clone());
        M o = n.remove(row);
        n.add(row + 1, o);
        setModelUndoableProperty(ARRAY, n);
    }

}
