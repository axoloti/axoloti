package axoloti.patchbank;

import axoloti.mvc.AbstractController;
import axoloti.mvc.IView;
import java.util.List;

/**
 *
 * @author jtaelman
 */
public class PatchBankController extends AbstractController<PatchBankModel, IView> {

    protected PatchBankController(PatchBankModel model) {
        super(model);
    }

    public void changePatchBankFiles(List files) {
        setModelUndoableProperty(PatchBankModel.FILES, files);
    }
}
