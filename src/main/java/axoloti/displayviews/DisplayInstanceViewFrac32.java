package axoloti.displayviews;

import axoloti.displays.DisplayInstanceController;
import axoloti.displays.DisplayInstanceFrac32;
import java.beans.PropertyChangeEvent;

abstract class DisplayInstanceViewFrac32 extends DisplayInstanceView1 {

    DisplayInstanceFrac32 displayInstance;

    DisplayInstanceViewFrac32(DisplayInstanceFrac32 displayInstance, DisplayInstanceController controller) {
        super(displayInstance, controller);
        this.displayInstance = displayInstance;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        updateV();
    }
}
