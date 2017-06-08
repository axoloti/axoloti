package axoloti;

import axoloti.mvc.AbstractView;

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
    public PatchController getController() {
        return controller;
    }

    PatchFrame patchFrame;

    public void setPatchFrame(PatchFrame patchFrame) {
        this.patchFrame = patchFrame;
    }

    public PatchFrame getPatchFrame() {
        return patchFrame;
    }
}
