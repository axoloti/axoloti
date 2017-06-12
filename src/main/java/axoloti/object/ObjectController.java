package axoloti.object;

import axoloti.atom.AtomController;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.array.ArrayController;

/**
 *
 * @author jtaelman
 */
public class ObjectController extends AbstractController {

    public ArrayController<AtomController> inlets;
    public ArrayController<AtomController> outlets;
    public ArrayController<AtomController> attrs;
    public ArrayController<AtomController> params;
    public ArrayController<AtomController> disps;
    
    public ObjectController(AxoObjectAbstract model, AbstractDocumentRoot documentRoot) {
        super(model, documentRoot);
        inlets = new ArrayController<>(model.getInlets(), documentRoot);
        outlets = new ArrayController<>(model.getOutlets(), documentRoot);
        if (model instanceof AxoObject) {
            AxoObject m = (AxoObject)model;
            attrs = new ArrayController<>(m.attributes, documentRoot);
            params = new ArrayController<>(m.params, documentRoot);
            disps = new ArrayController<>(m.displays, documentRoot);
        }
    }
    
    @Override 
    public AxoObjectAbstract getModel(){
        return (AxoObjectAbstract)super.getModel();
    }
}
