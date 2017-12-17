package axoloti.piccolo.displayviews;

import axoloti.patch.object.display.DisplayInstanceInt32;
import axoloti.abstractui.IAxoObjectInstanceView;

public abstract class PDisplayInstanceViewInt32 extends PDisplayInstanceView1 {

    DisplayInstanceInt32 displayInstance;

    PDisplayInstanceViewInt32(DisplayInstanceInt32 displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
        this.displayInstance = displayInstance;
    }
}
