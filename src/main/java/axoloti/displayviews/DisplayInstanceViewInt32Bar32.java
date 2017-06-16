package axoloti.displayviews;

import axoloti.displays.DisplayInstanceController;
import axoloti.displays.DisplayInstanceInt32Bar32;
import components.displays.LedstripComponent;

class DisplayInstanceViewInt32Bar32 extends DisplayInstanceViewInt32 {

    private LedstripComponent readout;

    public DisplayInstanceViewInt32Bar32(DisplayInstanceController controller) {
        super(controller);
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        readout = new LedstripComponent(0, 32);
        add(readout);
        readout.setSize(readout.getHeight(), 80);
    }

    @Override
    public void updateV() {
        int i = getModel().getValueRef().getInt();
        if ((i >= 0) && (i < 32)) {
            readout.setValue(1 << i);
        } else {
            readout.setValue(0);
        }
    }
}
