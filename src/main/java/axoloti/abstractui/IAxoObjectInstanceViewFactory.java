package axoloti.abstractui;

import axoloti.patch.object.IAxoObjectInstance;

public interface IAxoObjectInstanceViewFactory {

    IAxoObjectInstanceView createView(
            IAxoObjectInstance obj, PatchView pv);
}
