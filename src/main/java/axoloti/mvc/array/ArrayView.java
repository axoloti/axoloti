package axoloti.mvc.array;

import axoloti.mvc.IModel;
import axoloti.mvc.IView;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jtaelman
 */
public abstract class ArrayView<T extends IView, M extends IModel> {

    public List<T> sync(List<T> existingViews, List models) {
        List<M> models1 = (List<M>) models;
        ArrayList<T> subviews2;
        if (existingViews == null) {
            subviews2 = new ArrayList<>();
        } else {
            subviews2 = new ArrayList<>(existingViews);
            for (T view : existingViews) {
                if (!models.contains(view.getDModel())) {
                    subviews2.remove(view);
                    view.dispose();
                    removeView(view);
                }
            }
        }
        ArrayList<T> subviews = new ArrayList<>();
        for (M model : models1) {
            // do we have a view already?
            T view = null;
            for (T view2 : subviews2) {
                if (model == view2.getDModel()) {
                    view = view2;
                    break;
                }
            }
            if (view == null) {
                view = viewFactory(model);
                // the factory method is assumed to add the view to controller
            }
            subviews.add(view);
        }
        if (!subviews.equals(existingViews)) {
            updateUI(subviews);
        }
        if (subviews.size() != models.size()) {
            throw new Error("sync error");
        }
        return subviews;
    }

    protected abstract void updateUI(List<T> views);

    /**
     * Override this method to create a suitable view of the model The
     * implementation should also call the addView method of the controller.
     */
    protected abstract T viewFactory(M model);

    protected abstract void removeView(T view);

}
