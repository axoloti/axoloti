package axoloti;

import java.beans.PropertyChangeEvent;
import axoloti.mvc.IView;

/**
 *
 * @author jtaelman
 */
public class PatchViewLive implements IView<PatchController> {

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
