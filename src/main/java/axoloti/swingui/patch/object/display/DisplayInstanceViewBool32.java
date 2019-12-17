package axoloti.swingui.patch.object.display;

import axoloti.patch.object.display.DisplayInstance;
import axoloti.swingui.components.displays.LedstripComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewBool32 extends DisplayInstanceViewInt32 {

    private LedstripComponent readout;

    DisplayInstanceViewBool32(DisplayInstance displayInstance) {
        super(displayInstance);
        initComponents();
    }

    private void initComponents() {

        readout = new LedstripComponent(0, 1);
        add(readout);
        readout.setSize(readout.getHeight(), 80);
        setSize(getPreferredSize());
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (DisplayInstance.DISP_VALUE.is(evt)) {
            int i = (Integer) evt.getNewValue();
            readout.setValue(i > 0 ? 1 : 0);
        }
    }
}
