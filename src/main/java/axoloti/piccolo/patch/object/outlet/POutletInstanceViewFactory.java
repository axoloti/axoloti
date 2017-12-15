package axoloti.piccolo.patch.object.outlet;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.abstractui.IIoletInstanceView;
import axoloti.patch.object.iolet.IoletInstanceController;

public class POutletInstanceViewFactory {

    public static IIoletInstanceView createView(IoletInstanceController controller, IAxoObjectInstanceView obj) {
        IIoletInstanceView view = new POutletInstanceView(controller, obj);
        view.PostConstructor();
        controller.addView(view);
        return view;
    }

}
