package axoloti.displayviews;

import axoloti.displays.DisplayInstance;
import axoloti.displays.DisplayInstanceController;
import components.displays.LedstripComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewInt32Bar16 extends DisplayInstanceViewInt32 {

    private LedstripComponent readout;

    DisplayInstanceViewInt32Bar16(DisplayInstanceController controller) {
        super(controller);
    }

    @Override
    void PostConstructor() {
        super.PostConstructor();

        readout = new LedstripComponent(0, 16);
        add(readout);
        readout.setSize(readout.getHeight(), 80);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (DisplayInstance.DISP_VALUE.is(evt)) {
            int i = (Integer) evt.getNewValue();
            if ((i >= 0) && (i < 16)) {
                readout.setValue(1 << i);
            } else {
                readout.setValue(0);
            }
        }
    }

}
