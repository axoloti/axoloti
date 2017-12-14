package axoloti;

import axoloti.mvc.IView;
import java.beans.PropertyChangeEvent;

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
    
    @Override
    public void dispose() {
    }
}
