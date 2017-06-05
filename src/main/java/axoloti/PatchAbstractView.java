package axoloti;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractView;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author jtaelman
 */
public class PatchAbstractView implements AbstractView {

    final PatchController controller;

    PatchAbstractView(PatchModel model, PatchController controller) {
        this.controller = controller;
    }
   
    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
    }

    @Override
    public AbstractController getController() {
    }
    
}
