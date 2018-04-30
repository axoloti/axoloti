package axoloti.swingui.patch.object.display;

import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.display.DisplayInstanceController;
import axoloti.swingui.components.displays.LedstripComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewInt32Bar16 extends DisplayInstanceViewInt32 {

    private LedstripComponent readout;

    DisplayInstanceViewInt32Bar16(DisplayInstanceController controller) {
        super(controller);
        initComponents();
    }

    private void initComponents() {
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
