package axoloti.displayviews;

import axoloti.displays.DisplayInstance1;
import axoloti.displays.DisplayInstanceController;

abstract class DisplayInstanceView1 extends DisplayInstanceView {
    DisplayInstance1 displayInstance;
    
    DisplayInstanceView1(DisplayInstance1 displayInstance, DisplayInstanceController controller) {
        super(displayInstance, controller);
        this.displayInstance = displayInstance;
    }
}