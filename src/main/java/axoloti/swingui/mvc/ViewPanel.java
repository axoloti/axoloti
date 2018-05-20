package axoloti.swingui.mvc;

import axoloti.mvc.IModel;
import axoloti.mvc.IView;
import javax.swing.JPanel;

/**
 *
 * @author jtaelman
 */
public abstract class ViewPanel<T extends IModel> extends JPanel implements IView<T> {

    final protected T model;

    public ViewPanel(T model) {
        this.model = model;
    }

    @Override
    public T getDModel() {
        return model;
    }

}
