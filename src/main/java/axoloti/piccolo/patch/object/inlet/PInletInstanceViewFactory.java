package axoloti.piccolo.patch.object.inlet;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.abstractui.IIoletInstanceView;
import axoloti.patch.object.iolet.IoletInstanceController;

public class PInletInstanceViewFactory {

    public static IIoletInstanceView createView(IoletInstanceController controller, IAxoObjectInstanceView obj) {
        IIoletInstanceView view = new PInletInstanceView(controller, obj);
        controller.addView(view);
        return view;
    }
}
