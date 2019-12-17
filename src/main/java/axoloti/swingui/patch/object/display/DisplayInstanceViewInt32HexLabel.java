package axoloti.swingui.patch.object.display;

import axoloti.patch.object.display.DisplayInstance;
import axoloti.swingui.components.LabelComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewInt32HexLabel extends DisplayInstanceViewInt32 {

    private LabelComponent readout;

    DisplayInstanceViewInt32HexLabel(DisplayInstance displayInstance) {
        super(displayInstance);
        initComponents();
    }

    private void initComponents() {
        readout = new LabelComponent("xxxxxxxxxxx");
        add(readout);
        readout.setSize(getSize());
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (DisplayInstance.DISP_VALUE.is(evt)) {
            int i = (Integer) evt.getNewValue();
            readout.setText(String.format("0x%08X", i));
        }
    }
}
