
package axoloti.displays;

import axoloti.displayviews.IDisplayInstanceView;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;

/**
 *
 * @author jtaelman
 */
public class DisplayInstanceController extends AbstractController<DisplayInstance, IDisplayInstanceView> {

    public DisplayInstanceController(DisplayInstance model, AbstractDocumentRoot documentRoot) {
        super(model, documentRoot);
    }
    
}
