package axoloti.displayviews;

import axoloti.displays.DisplayInstanceBool32;
import components.displays.LedstripComponent;

public class DisplayInstanceViewBool32 extends DisplayInstanceViewInt32 {

    DisplayInstanceBool32 displayInstance;
    private LedstripComponent readout;

    public DisplayInstanceViewBool32(DisplayInstanceBool32 displayInstance) {
        super(displayInstance);
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
