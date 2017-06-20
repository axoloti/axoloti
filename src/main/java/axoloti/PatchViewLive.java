package axoloti;

import axoloti.mvc.AbstractView;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author jtaelman
 */
public class PatchViewLive implements AbstractView<PatchController> {

    final PatchController controller;
    
    public PatchViewLive(PatchController controller) {
        this.controller = controller;
    }
    
    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        
    }

    @Override
    public PatchController getController() {
        return controller;
    }
    
}
