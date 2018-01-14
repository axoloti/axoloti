package axoloti.patch.object.iolet;

import axoloti.abstractui.IIoletInstanceView;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.patch.object.ObjectInstanceController;

public class IoletInstanceController extends AbstractController<IoletInstance, IIoletInstanceView, ObjectInstanceController> {

    public IoletInstanceController(IoletInstance model, AbstractDocumentRoot documentRoot, ObjectInstanceController parent) {
        super(model, documentRoot, parent);
    }
}
