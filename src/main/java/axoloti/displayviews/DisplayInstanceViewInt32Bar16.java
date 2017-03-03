package axoloti.displayviews;

import axoloti.displays.DisplayInstanceInt32Bar16;
import components.displays.LedstripComponent;

public class DisplayInstanceViewInt32Bar16 extends DisplayInstanceViewInt32 {
    private DisplayInstanceInt32Bar16 displayInstance;
    private LedstripComponent readout;
    
    public DisplayInstanceViewInt32Bar16(DisplayInstanceInt32Bar16 displayInstance) {
        super(displayInstance);
        this.displayInstance = displayInstance;
    }
    
    @Override
    public void PostConstructor() {
        super.PostConstructor();

        readout = new LedstripComponent(0, 16);
        add(readout);
        readout.setSize(readout.getHeight(), 80);
    }
    
    @Override
    public void updateV() {
        int i = displayInstance.getValueRef().getInt();
        if ((i >= 0) && (i < 16)) {
            readout.setValue(1 << i);
        } else {
            readout.setValue(0);
        }
    }
}