package axoloti.piccolo.displayviews;

import axoloti.patch.object.display.DisplayInstanceFrac32;
import axoloti.abstractui.IAxoObjectInstanceView;

public abstract class PDisplayInstanceViewFrac32 extends PDisplayInstanceView1 {

    DisplayInstanceFrac32 displayInstance;

    PDisplayInstanceViewFrac32(DisplayInstanceFrac32 displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
        this.displayInstance = displayInstance;
    }
}
