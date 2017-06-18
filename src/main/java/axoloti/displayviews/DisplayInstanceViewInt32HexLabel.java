package axoloti.displayviews;

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

        readout = new LabelComponent("0xxxxxxxxx");
        add(readout);
        readout.setSize(80, 18);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (evt.getPropertyName().equals(DisplayInstanceController.DISP_VALUE)) {
            int i = (Integer) evt.getNewValue();
            readout.setText(String.format("0x%08X", i));
        }
    }
}
