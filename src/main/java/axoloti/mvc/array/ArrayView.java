package axoloti.mvc.array;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractModel;
import axoloti.mvc.AbstractView;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author jtaelman
 */
public abstract class ArrayView<T extends AbstractView> implements AbstractView<ArrayController>, Iterable<T> {

    ArrayController<AbstractController, AbstractModel, AbstractController> controller;
    List<T> subviews = new ArrayList<>();

    public ArrayView(ArrayController controller) {
        this.controller = controller;
        subviews = new ArrayList<>();
        Sync();
    }

    public ArrayView(ArrayController controller, List<T> subviews) {
        this.controller = controller;
        this.subviews = subviews;
        Sync();
    }

    private void Sync() {
        ArrayList<T> subviews2 = new ArrayList<T>(subviews);
        for (T view : subviews) {
            if (!controller.subcontrollers.contains(view.getController())) {
                subviews2.remove(view);
                removeView(view);
            }
        }
        subviews.clear();
        for (AbstractController ctrl : controller) {
            // do we have a view already?
            T view = null;
            for (T view2 : subviews2) {
                if (ctrl == view2.getController()) {
                    view = view2;
                    break;
                }
            }
            if (view == null) {
                view = viewFactory(ctrl);
                // the factory method is assumed to add the view to controller
            }
            subviews.add(view);
        }
        updateUI();
    }

    public abstract void updateUI();

    public List<T> getSubViews() {
        return subviews;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ArrayController.ARRAY)) {
            Sync();
        }
    }

    @Override
    public ArrayController getController() {
        return controller;
    }

    public abstract T viewFactory(AbstractController ctrl);

    public abstract void removeView(T view);

    @Override
    public Iterator<T> iterator() {
        return subviews.iterator();
    }

    public T getViewOfModel(Object model) {
        for (T v : subviews) {
            if (v.getController().getModel() == model) {
                return v;
            }
        }
        return null;
    }

}
