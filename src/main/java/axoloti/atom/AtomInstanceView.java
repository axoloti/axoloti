package axoloti.atom;

import axoloti.mvc.AbstractController;
import java.beans.PropertyChangeEvent;
import javax.swing.JPanel;
import axoloti.mvc.IView;

public abstract class AtomInstanceView extends JPanel implements IView {

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
    }

    @Override
    public abstract AbstractController getController();

}
