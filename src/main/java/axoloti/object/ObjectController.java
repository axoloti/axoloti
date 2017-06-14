package axoloti.object;

import axoloti.atom.AtomDefinitionController;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.AbstractView;
import axoloti.mvc.array.ArrayController;

/**
 *
 * @author jtaelman
 */
public class ObjectController extends AbstractController<AxoObjectAbstract, AbstractView> {

    public ArrayController<AtomDefinitionController> inlets;
    public ArrayController<AtomDefinitionController> outlets;
    public ArrayController<AtomDefinitionController> attrs;
    public ArrayController<AtomDefinitionController> params;
    public ArrayController<AtomDefinitionController> disps;

    public ObjectController(AxoObjectAbstract model, AbstractDocumentRoot documentRoot) {
        super(model, documentRoot);
        inlets = new ArrayController<>(model.getInlets(), documentRoot);
        outlets = new ArrayController<>(model.getOutlets(), documentRoot);
        if (model instanceof AxoObject) {
            AxoObject m = (AxoObject) model;
            attrs = new ArrayController<>(m.attributes, documentRoot);
            params = new ArrayController<>(m.params, documentRoot);
            disps = new ArrayController<>(m.displays, documentRoot);
        }
    }

}
