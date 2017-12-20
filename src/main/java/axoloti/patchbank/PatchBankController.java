package axoloti.patchbank;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.IView;

/**
 *
 * @author jtaelman
 */
public class PatchBankController extends AbstractController<PatchBankModel, IView, AbstractController> {

    public PatchBankController(PatchBankModel model, AbstractDocumentRoot documentRoot, AbstractController parent) {
        super(model, documentRoot, parent);
    }

}
