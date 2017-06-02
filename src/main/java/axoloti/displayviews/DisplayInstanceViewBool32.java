package axoloti.displayviews;

import axoloti.displays.DisplayInstanceBool32;
import axoloti.displays.DisplayInstanceController;
import components.displays.LedstripComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewBool32 extends DisplayInstanceViewInt32 {

    DisplayInstanceBool32 displayInstance;
    private LedstripComponent readout;

    public DisplayInstanceViewBool32(DisplayInstanceBool32 displayInstance, DisplayInstanceController controller) {
        super(displayInstance, controller);
        this.displayInstance = displayInstance;
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
        readout.setValue(displayInstance.getValueRef().getInt() > 0 ? 1 : 0);
    }
}
