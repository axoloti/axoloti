package axoloti.displayviews;

import axoloti.displays.DisplayInstanceController;
import axoloti.displays.DisplayInstanceFrac32;
import java.beans.PropertyChangeEvent;

abstract class DisplayInstanceViewFrac32 extends DisplayInstanceView1 {

    DisplayInstanceViewFrac32(DisplayInstanceController controller) {
        super(controller);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (evt.getPropertyName().equals(DisplayInstanceController.DISP_VALUE)) {
            updateV();
        }
    }

    @Override
    DisplayInstanceFrac32 getModel() {
        return (DisplayInstanceFrac32) super.getModel();
    }
}
