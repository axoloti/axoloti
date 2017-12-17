package axoloti.swingui.patch.object.display;

import axoloti.patch.object.display.DisplayInstanceController;
import axoloti.patch.object.display.DisplayInstanceInt32;

abstract class DisplayInstanceViewInt32 extends DisplayInstanceView1 {

    DisplayInstanceViewInt32(DisplayInstanceController controller) {
        super(controller);
    }

    @Override
    DisplayInstanceInt32 getModel() {
        return (DisplayInstanceInt32) super.getModel();
    }

}
