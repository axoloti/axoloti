
package axoloti.swingui.patch;

import axoloti.abstractui.PatchView;
import axoloti.patch.PatchController;
import static axoloti.patch.PatchViewType.PICCOLO;
import axoloti.piccolo.patch.PatchViewPiccolo;
import axoloti.preferences.Preferences;

/**
 *
 * @author jtaelman
 */
public class PatchViewFactory {

    public static PatchView patchViewFactory(PatchController patchController) {
        if (Preferences.getPreferences().getPatchViewType() == PICCOLO) {
            return new PatchViewPiccolo(patchController);
        } else {
            return new PatchViewSwing(patchController);
        }
    }
}
