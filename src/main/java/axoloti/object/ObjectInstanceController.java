package axoloti.object;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.AbstractView;

/**
 *
 * @author jtaelman
 */
public class ObjectInstanceController extends AbstractController<AxoObjectInstanceAbstract, AbstractView> {

    public ObjectInstanceController(AxoObjectInstanceAbstract model, AbstractDocumentRoot documentRoot) {
        super(model, documentRoot);
    }
    
}
