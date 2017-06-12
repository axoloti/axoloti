package axoloti.atom;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;

/**
 *
 * @author jtaelman
 */
public class AtomController extends AbstractController<AtomDefinition, AtomInstance> {

    public static final String ATOM_NAME = "Name";
    public static final String ATOM_DESCRIPTION = "Description";

    public AtomController(AtomDefinition model, AbstractDocumentRoot documentRoot) {
        super(model, documentRoot);
    }

    public void changeName(String name) {
        setModelUndoableProperty(ATOM_NAME, name);
    }

    public void changeDescription(String name) {
        setModelUndoableProperty(ATOM_DESCRIPTION, name);
    }

}
