package axoloti.displayviews;

import axoloti.displays.DisplayInstanceController;
import axoloti.displays.DisplayInstanceFrac32;

abstract class DisplayInstanceViewFrac32 extends DisplayInstanceView1 {

    DisplayInstanceViewFrac32(DisplayInstanceController controller) {
        super(controller);
    }

    @Override
    DisplayInstanceFrac32 getModel() {
        return (DisplayInstanceFrac32) super.getModel();
    }
}
