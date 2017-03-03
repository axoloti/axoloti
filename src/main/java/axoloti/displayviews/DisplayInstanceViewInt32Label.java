package axoloti.displayviews;

import axoloti.displays.DisplayInstanceInt32Label;
import components.LabelComponent;

public class DisplayInstanceViewInt32Label extends DisplayInstanceViewInt32 {
    private DisplayInstanceInt32Label displayInstance;
    private LabelComponent readout;

    public DisplayInstanceViewInt32Label(DisplayInstanceInt32Label displayInstance) {
        super(displayInstance);
        this.displayInstance = displayInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        readout = new LabelComponent("xxxxxx");
        add(readout);
        readout.setSize(80, 18);
    }

    @Override
    public void updateV() {
        readout.setText(":" + Integer.toString(displayInstance.getValueRef().getInt()));
    }
}
