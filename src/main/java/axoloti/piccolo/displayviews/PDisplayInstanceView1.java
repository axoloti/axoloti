package axoloti.piccolo.displayviews;

import axoloti.displays.DisplayInstance1;
import axoloti.objectviews.IAxoObjectInstanceView;

public abstract class PDisplayInstanceView1 extends PDisplayInstanceView {

    DisplayInstance1 displayInstance;

    PDisplayInstanceView1(DisplayInstance1 displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
        this.displayInstance = displayInstance;
    }
}
