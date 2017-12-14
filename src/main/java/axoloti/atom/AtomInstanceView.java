package axoloti.atom;

import axoloti.mvc.AbstractController;
import axoloti.mvc.IView;
import javax.swing.JPanel;

public abstract class AtomInstanceView extends JPanel implements IView {

    @Override
    public abstract AbstractController getController();

    @Override
    public void dispose() {
    }
}
