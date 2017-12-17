package axoloti.abstractui;

import axoloti.patch.PatchController;
import axoloti.mvc.View;
import axoloti.swingui.patch.PatchFrame;

/**
 *
 * @author jtaelman
 */
public abstract class PatchAbstractView extends View<PatchController> {


    PatchAbstractView(PatchController controller) {
        super(controller);
    }

    PatchFrame patchFrame;

    public void setPatchFrame(PatchFrame patchFrame) {
        this.patchFrame = patchFrame;
    }

    public PatchFrame getPatchFrame() {
        return patchFrame;
    }
}
