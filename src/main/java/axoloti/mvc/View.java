package axoloti.mvc;

/**
 *
 * @author jtaelman
 */
public abstract class View<T extends AbstractController> implements IView<T> {

    final T controller;

    @Override
    public T getController() {
        return controller;
    }

    public View(T controller) {
        this.controller = controller;
    }

}
