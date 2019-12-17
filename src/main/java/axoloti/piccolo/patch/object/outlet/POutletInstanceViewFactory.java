package axoloti.piccolo.patch.object.outlet;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.abstractui.IOutletInstanceView;
import axoloti.patch.object.outlet.OutletInstance;

public class POutletInstanceViewFactory {

    private POutletInstanceViewFactory() {
    }

    public static IOutletInstanceView createView(OutletInstance outletInstance, IAxoObjectInstanceView obj) {
        IOutletInstanceView view = new POutletInstanceView(outletInstance, obj);
        outletInstance.getController().addView(view);
        return view;
    }

}
