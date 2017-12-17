package axoloti.swingui.mvc;

import axoloti.mvc.AbstractController;
import axoloti.mvc.IView;
import javax.swing.JPanel;

/**
 *
 * @author jtaelman
 */
public abstract class ViewPanel<T extends AbstractController> extends JPanel implements IView<T> {

    final private T controller;

    public ViewPanel(T controller) {
        this.controller = controller;
    }

    @Override
    public T getController() {
        return controller;
    }

}
