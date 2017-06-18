package axoloti.displayviews;

import axoloti.displays.DisplayInstanceController;
import axoloti.displays.DisplayInstanceInt32;

abstract class DisplayInstanceViewInt32 extends DisplayInstanceView1 {

    DisplayInstanceViewInt32(DisplayInstanceController controller) {
        super(controller);
    }

    @Override
    DisplayInstanceInt32 getModel() {
        return (DisplayInstanceInt32) super.getModel();
    }

}
