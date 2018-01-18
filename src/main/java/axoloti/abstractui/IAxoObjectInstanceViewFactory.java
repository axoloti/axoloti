package axoloti.abstractui;

import axoloti.patch.object.ObjectInstanceController;

public interface IAxoObjectInstanceViewFactory {
    public abstract IAxoObjectInstanceView createView(
        ObjectInstanceController controller, PatchView pv);
}
