package axoloti.piccolo.displayviews;

import axoloti.patch.object.display.DisplayInstance1;
import axoloti.abstractui.IAxoObjectInstanceView;

public abstract class PDisplayInstanceView1 extends PDisplayInstanceView {

    DisplayInstance1 displayInstance;

    PDisplayInstanceView1(DisplayInstance1 displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
        this.displayInstance = displayInstance;
    }
}
