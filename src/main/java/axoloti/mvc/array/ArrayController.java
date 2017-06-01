package axoloti.mvc.array;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.AbstractModel;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

/**
 *
 * @author jtaelman
 */
public class ArrayController extends AbstractController<ArrayModel, ArrayView> {

    static final String ARRAY = "Array";

    ArrayList<AbstractController> subcontrollers = new ArrayList<>();

    public ArrayController(ArrayModel model, AbstractDocumentRoot documentRoot) {
        super(model, documentRoot);
        ArrayList<AbstractModel> am = model.getArray();
        for (AbstractModel m : am) {
            subcontrollers.add(m.createController(documentRoot));
        }
    }

    public void add(AbstractModel m) {
        subcontrollers.add(m.createController(getDocumentRoot()));
        ArrayList<AbstractModel> n = (ArrayList<AbstractModel>) (getModel().getArray().clone());
        n.add(m);
        setModelUndoableProperty(ARRAY, n);
    }

    void removeLast() {
        subcontrollers.remove(subcontrollers.size() - 1);
        ArrayList<AbstractModel> n = (ArrayList<AbstractModel>) (getModel().getArray().clone());
        n.remove(n.size() - 1);
        setModelUndoableProperty(ARRAY, n);
    }

    void updateSubcontrollers() {
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
            subcontrollers.add(ctrl);
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
        updateSubcontrollers();
        super.propertyChange(evt);
    }

}
