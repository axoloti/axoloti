package axoloti.swingui.patch.object.display;

import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.display.DisplayInstanceInt32;

abstract class DisplayInstanceViewInt32 extends DisplayInstanceView1 {

    DisplayInstanceViewInt32(DisplayInstance displayInstance) {
        super(displayInstance);
    }

    @Override
    public DisplayInstanceInt32 getDModel() {
        return (DisplayInstanceInt32) super.getDModel();
    }

}
