package axoloti.displayviews;

import axoloti.displays.DisplayInstanceBool32;
import axoloti.displays.DisplayInstanceController;
import components.displays.LedstripComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewBool32 extends DisplayInstanceViewInt32 {

    private LedstripComponent readout;

    public DisplayInstanceViewBool32(DisplayInstanceController controller) {
        super(controller);
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        readout = new LedstripComponent(0, 1);
        add(readout);
        readout.setSize(readout.getHeight(), 80);
        setSize(getPreferredSize());
    }

    @Override
    public void updateV() {
        readout.setValue(getModel().getValueRef().getInt() > 0 ? 1 : 0);
    }
}
