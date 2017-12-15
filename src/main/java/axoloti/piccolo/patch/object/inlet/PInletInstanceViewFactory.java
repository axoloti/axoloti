package axoloti.piccolo.patch.object.inlet;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.abstractui.IIoletInstanceView;
import axoloti.patch.object.iolet.IoletInstanceController;

public class PInletInstanceViewFactory {

    public static IIoletInstanceView createView(IoletInstanceController controller, IAxoObjectInstanceView obj) {
        IIoletInstanceView view = new PInletInstanceView(controller, obj);
        view.PostConstructor();
        controller.addView(view);
        return view;
    }
}
