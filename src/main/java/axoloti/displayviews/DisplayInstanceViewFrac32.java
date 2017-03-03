package axoloti.displayviews;

import axoloti.displays.DisplayInstanceFrac32;

public abstract class DisplayInstanceViewFrac32 extends DisplayInstanceView1 {

    DisplayInstanceFrac32 displayInstance;

    DisplayInstanceViewFrac32(DisplayInstanceFrac32 displayInstance) {
        super(displayInstance);
        this.displayInstance = displayInstance;
    }
}
