package axoloti.atom;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.object.ObjectController;

/**
 *
 * @author jtaelman
 */
public class AtomDefinitionController extends AbstractController<AtomDefinition, AtomInstance, ObjectController> {

    public AtomDefinitionController(AtomDefinition model, AbstractDocumentRoot documentRoot, ObjectController parent) {
        super(model, documentRoot, parent);
    }

}
