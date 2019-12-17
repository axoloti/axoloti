package axoloti.mvc;

/**
 *
 * @author jtaelman
 */
public abstract class View<M extends IModel> implements IView<M> {

    final protected M model;

    @Override
    public M getDModel() {
        return model;
    }

    public View(M model) {
        this.model = model;
    }

}
