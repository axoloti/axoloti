package axoloti.abstractui;

import axoloti.patch.object.ObjectInstanceController;

public interface IAxoObjectInstanceViewFactory {

    IAxoObjectInstanceView createView(
            ObjectInstanceController controller, PatchView pv);
}
