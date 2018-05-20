package axoloti.piccolo.patch.object.display;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.display.DisplayInstance1;

abstract class PDisplayInstanceView1 extends PDisplayInstanceView {

    DisplayInstance1 displayInstance;

    PDisplayInstanceView1(DisplayInstance displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
    }
}
