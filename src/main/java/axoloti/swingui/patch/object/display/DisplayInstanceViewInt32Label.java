package axoloti.swingui.patch.object.display;

import axoloti.patch.object.display.DisplayInstance;
import axoloti.swingui.components.LabelComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewInt32Label extends DisplayInstanceViewInt32 {

    private LabelComponent readout;

    DisplayInstanceViewInt32Label(DisplayInstance displayInstance) {
        super(displayInstance);
        initComponents();
    }

    private void initComponents() {
        readout = new LabelComponent("xxxxxx");
        add(readout);
        readout.setSize(80, 18);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (DisplayInstance.DISP_VALUE.is(evt)) {
            int i = (Integer) evt.getNewValue();
            readout.setText(":" + Integer.toString(i));
        }
    }
}
