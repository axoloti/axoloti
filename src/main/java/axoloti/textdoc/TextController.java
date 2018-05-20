package axoloti.textdoc;

import axoloti.mvc.AbstractController;
import axoloti.mvc.IView;

/**
 *
 * @author jtaelman
 */
public class TextController extends AbstractController<TextModel, IView> {

    public TextController(TextModel model) {
        super(model);
    }

}
