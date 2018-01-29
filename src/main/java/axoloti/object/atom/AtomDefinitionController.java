package axoloti.object.atom;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.object.ObjectController;
import axoloti.patch.object.atom.AtomInstance;

/**
 *
 * @author jtaelman
 */
public class AtomDefinitionController extends AbstractController<AtomDefinition, AtomInstance, ObjectController> {

    public AtomDefinitionController(AtomDefinition model, AbstractDocumentRoot documentRoot, ObjectController parent) {
        super(model, documentRoot, parent);
    }

}
