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
public class ArrayController<T extends AbstractController> extends AbstractController<ArrayModel, ArrayView> implements Iterable<T> {

    static final String ARRAY = "Array";

    ArrayList<T> subcontrollers = new ArrayList<>();

    public ArrayController(ArrayModel model, AbstractDocumentRoot documentRoot) {
        super(model, documentRoot);
        ArrayList<AbstractModel> am = model.getArray();
        for (AbstractModel m : am) {
            subcontrollers.add((T) m.createController(documentRoot));
        }
    }

    public T add(AbstractModel m) {
        AbstractController c = m.createController(getDocumentRoot());
        subcontrollers.add((T) c);
        ArrayList<AbstractModel> n = (ArrayList<AbstractModel>) (getModel().getArray().clone());
        n.add(m);
        setModelUndoableProperty(ARRAY, n);
        return (T) c;
    }

    public boolean remove(AbstractModel m) {
        ArrayList<AbstractModel> n = (ArrayList<AbstractModel>) (getModel().getArray().clone());
        boolean r = n.remove(m);
        if (r) {
            setModelUndoableProperty(ARRAY, n);
        }
        return r;
    }

    public void syncControllers() {
        ArrayList<AbstractController> subcontrollers2 = (ArrayList<AbstractController>) subcontrollers.clone();
        subcontrollers = new ArrayList<>();
        for (Object o : getModel().array) {
            AbstractModel om = (AbstractModel) o;
            AbstractController ctrl = null;
            for (AbstractController ctrl2 : subcontrollers2) {
                if (om == ctrl2.getModel()) {
                    ctrl = ctrl2;
                    break;
                }
            }
            if (ctrl == null) {
                ctrl = om.createController(getDocumentRoot());
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

}
