package axoloti.mvc.array;

import axoloti.mvc.AbstractController;
import axoloti.mvc.IView;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jtaelman
 */
public abstract class ArrayView<T extends IView> {

    public List<T> Sync(List<T> existingViews, ArrayController controller) {
        ArrayList<T> subviews2;
        if (existingViews == null) {
            subviews2 = new ArrayList<T>();
        } else {
            subviews2 = new ArrayList<T>(existingViews);
            for (T view : existingViews) {
                if (!controller.subcontrollers.contains(view.getController())) {
                    subviews2.remove(view);
                    view.dispose();
                    removeView(view);
                }
            }
        }
        ArrayList<T> subviews = new ArrayList<T>();
        for (Object ctrl : controller.subcontrollers) {
            // do we have a view already?
            T view = null;
            for (T view2 : subviews2) {
                if (ctrl == view2.getController()) {
                    view = view2;
                    break;
                }
            }
            if (view == null) {
                view = viewFactory((AbstractController) ctrl);
                // the factory method is assumed to add the view to controller
            }
            subviews.add(view);
        }
        T views[] = (T[]) subviews.toArray(new IView[]{});
        updateUI(subviews);
        if (subviews.size() != controller.subcontrollers.size()) {
            throw new Error("sync error");
        }
        return subviews;
    }

    public abstract void updateUI(List<T> views);


    /* Override this method to create a suitable view of the model referenced 
     * by the controller. The implementation should also call the addView method 
     * of the controller. */
    public abstract T viewFactory(AbstractController ctrl);

    public abstract void removeView(T view);

}
