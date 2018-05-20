package axoloti.object.atom;

import axoloti.mvc.AbstractController;
import axoloti.mvc.IView;

/**
 *
 * @author jtaelman
 */
public class AtomDefinitionController extends AbstractController<AtomDefinition, IView> {

    AtomDefinitionController(AtomDefinition model) {
        super(model);
    }

    public void changeName(String name) {
        setModelUndoableProperty(AtomDefinition.NAME, name);
    }

    public void changeDescription(String name) {
        setModelUndoableProperty(AtomDefinition.DESCRIPTION, name);
    }
}
