package axoloti.displayviews;

import axoloti.displays.DisplayInstance;
import axoloti.displays.DisplayInstanceController;
import components.LabelComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewInt32HexLabel extends DisplayInstanceViewInt32 {

    private LabelComponent readout;

    DisplayInstanceViewInt32HexLabel(DisplayInstanceController controller) {
        super(controller);
    }

    @Override
    void PostConstructor() {
        super.PostConstructor();

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
