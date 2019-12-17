package axoloti.swingui.patch.object.display;

import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.display.DisplayInstanceFrac32;

abstract class DisplayInstanceViewFrac32 extends DisplayInstanceView1 {

    DisplayInstanceViewFrac32(DisplayInstance displayInstance) {
        super(displayInstance);
    }

    @Override
    public DisplayInstanceFrac32 getDModel() {
        return (DisplayInstanceFrac32) super.getDModel();
    }
}
