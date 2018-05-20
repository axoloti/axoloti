package axoloti.piccolo.patch.object.display;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.display.DisplayInstanceInt32;

abstract class PDisplayInstanceViewInt32 extends PDisplayInstanceView1 {

    DisplayInstanceInt32 displayInstance;

    PDisplayInstanceViewInt32(DisplayInstance displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
    }
}
