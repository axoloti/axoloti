package axoloti;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractView;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author jtaelman
 */
public abstract class PatchAbstractView implements AbstractView {

    final PatchController controller;

    PatchAbstractView(PatchModel model, PatchController controller) {
        this.controller = controller;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AbstractController getController() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    PatchFrame patchFrame;

    public void setPatchFrame(PatchFrame patchFrame) {
        this.patchFrame = patchFrame;
    }   

    public PatchFrame getPatchFrame() {
        return patchFrame;
    }
}
