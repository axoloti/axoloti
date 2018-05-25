
package axoloti.swingui.patch;

import axoloti.abstractui.PatchView;
import axoloti.patch.PatchModel;
import static axoloti.patch.PatchViewType.PICCOLO;
import axoloti.piccolo.patch.PatchViewPiccolo;
import axoloti.preferences.Preferences;

/**
 *
 * @author jtaelman
 */
public class PatchViewFactory {

    private PatchViewFactory() {
    }

    public static PatchView patchViewFactory(PatchModel patchModel) {
        if (Preferences.getPreferences().getPatchViewType() == PICCOLO) {
            return new PatchViewPiccolo(patchModel);
        } else {
            return new PatchViewSwing(patchModel);
        }
    }
}
