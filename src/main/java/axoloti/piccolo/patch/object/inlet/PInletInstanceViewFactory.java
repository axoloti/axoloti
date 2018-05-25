package axoloti.piccolo.patch.object.inlet;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.abstractui.IInletInstanceView;
import axoloti.patch.object.inlet.InletInstance;

public class PInletInstanceViewFactory {

    private PInletInstanceViewFactory() {
    }

    public static IInletInstanceView createView(InletInstance inletInstance, IAxoObjectInstanceView obj) {
        IInletInstanceView view = new PInletInstanceView(inletInstance, obj);
        inletInstance.getController().addView(view);
        return view;
    }
}
