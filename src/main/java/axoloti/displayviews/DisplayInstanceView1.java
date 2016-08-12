package axoloti.displayviews;

import axoloti.displays.DisplayInstance1;

public abstract class DisplayInstanceView1 extends DisplayInstanceView {
    DisplayInstance1 displayInstance;
    
    DisplayInstanceView1(DisplayInstance1 displayInstance) {
        super(displayInstance);
        this.displayInstance = displayInstance;
    }
}