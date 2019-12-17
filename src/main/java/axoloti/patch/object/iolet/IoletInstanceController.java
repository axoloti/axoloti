package axoloti.patch.object.iolet;

import axoloti.abstractui.IIoletInstanceView;
import axoloti.mvc.AbstractController;
import axoloti.patch.object.ObjectInstanceController;

public class IoletInstanceController extends AbstractController<IoletInstance, IIoletInstanceView> {

    protected IoletInstanceController(IoletInstance model, ObjectInstanceController parent) {
        super(model);
    }

    public void changeConnected(boolean connected) {
        setModelUndoableProperty(IoletInstance.CONNECTED, connected);
    }
}
