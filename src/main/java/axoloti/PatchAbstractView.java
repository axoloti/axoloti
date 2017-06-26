package axoloti;

import axoloti.mvc.IView;

/**
 *
 * @author jtaelman
 */
public abstract class PatchAbstractView implements IView<PatchController> {

    final PatchController controller;

    PatchAbstractView(PatchController controller) {
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
