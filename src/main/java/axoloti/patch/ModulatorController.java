package axoloti.patch;

import axoloti.mvc.AbstractController;
import axoloti.mvc.IView;

/**
 *
 * @author jtaelman
 */
public class ModulatorController extends AbstractController<Modulator, IView> {

    public ModulatorController(Modulator model) {
        super(model);
    }

    public void addModulation(Modulation m) {
        addUndoableElementToList(Modulator.MODULATIONS, m);
    }

    public void removeModulation(Modulation m) {
        removeUndoableElementFromList(Modulator.MODULATIONS, m);
    }

}
