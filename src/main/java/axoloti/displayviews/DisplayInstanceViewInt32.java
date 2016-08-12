package axoloti.displayviews;

import axoloti.displays.DisplayInstanceInt32;

public abstract class DisplayInstanceViewInt32 extends DisplayInstanceView1 {
    DisplayInstanceInt32 displayInstance;
    
    DisplayInstanceViewInt32(DisplayInstanceInt32 displayInstance) {
        super(displayInstance);
        this.displayInstance = displayInstance;
    }
}