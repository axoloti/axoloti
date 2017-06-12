package axoloti.atom;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractView;
import java.beans.PropertyChangeEvent;
import javax.swing.JPanel;

public abstract class AtomInstanceView extends JPanel implements AbstractView {

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
    }

    @Override
    public abstract AbstractController getController();

}
