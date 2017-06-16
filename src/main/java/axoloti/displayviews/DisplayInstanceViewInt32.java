package axoloti.displayviews;

import axoloti.displays.DisplayInstance;
import axoloti.displays.DisplayInstanceController;
import axoloti.displays.DisplayInstanceInt32;
import java.beans.PropertyChangeEvent;

abstract class DisplayInstanceViewInt32 extends DisplayInstanceView1 {

    DisplayInstanceViewInt32(DisplayInstanceController controller) {
        super(controller);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        updateV();
    }

    @Override
    DisplayInstanceInt32 getModel() {
        return (DisplayInstanceInt32) super.getModel();
    }

}
