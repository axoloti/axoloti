package axoloti.piccolo.patch.object.display;

import axoloti.patch.object.display.DisplayInstance1;
import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.display.DisplayInstanceController;

public abstract class PDisplayInstanceView1 extends PDisplayInstanceView {

    DisplayInstance1 displayInstance;

    PDisplayInstanceView1(DisplayInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }
}
