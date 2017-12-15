package axoloti.piccolo.patch.object.display;

import axoloti.patch.object.display.DisplayInstanceInt32;
import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.display.DisplayInstanceController;

public abstract class PDisplayInstanceViewInt32 extends PDisplayInstanceView1 {

    DisplayInstanceInt32 displayInstance;

    PDisplayInstanceViewInt32(DisplayInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }
}
