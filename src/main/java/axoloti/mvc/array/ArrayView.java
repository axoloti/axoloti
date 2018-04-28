package axoloti.mvc.array;

import axoloti.mvc.AbstractController;
import axoloti.mvc.IModel;
import axoloti.mvc.IView;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jtaelman
 */
public abstract class ArrayView<T extends IView> {

    public List<T> Sync(List<T> existingViews, List models) {
        List<IModel> models1 = (List<IModel>)models;
        ArrayList<T> subviews2;
        if (existingViews == null) {
            subviews2 = new ArrayList<T>();
        } else {
            subviews2 = new ArrayList<T>(existingViews);
            for (T view : existingViews) {
                if (!models.contains(view.getController().getModel())) {
                    subviews2.remove(view);
                    view.dispose();
                    removeView(view);
                }
            }
        }
        ArrayList<T> subviews = new ArrayList<T>();
        for (IModel model : models1) {
            // do we have a view already?
            T view = null;
            for (T view2 : subviews2) {
                if (model.getControllerFromModel() == view2.getController()) {
                    view = view2;
                    break;
                }
            }
            if (view == null) {
                view = viewFactory((AbstractController) model.getControllerFromModel());
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

    /* Override this method to create a suitable view of the model referenced
     * by the controller. The implementation should also call the addView method
     * of the controller. */
    protected abstract T viewFactory(AbstractController ctrl);

    protected abstract void removeView(T view);

}
