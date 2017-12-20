package axoloti.textdoc;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.IView;

/**
 *
 * @author jtaelman
 */
public class TextController extends AbstractController<TextModel, IView, AbstractController> {

    public TextController(TextModel model, AbstractDocumentRoot documentRoot, AbstractController parent) {
        super(model, documentRoot, parent);
    }

}
