package axoloti.displayviews;

import axoloti.displays.DisplayInstanceController;
import axoloti.displays.DisplayInstanceInt32Label;
import components.LabelComponent;

class DisplayInstanceViewInt32Label extends DisplayInstanceViewInt32 {
    private DisplayInstanceInt32Label displayInstance;
    private LabelComponent readout;

    public DisplayInstanceViewInt32Label(DisplayInstanceInt32Label displayInstance, DisplayInstanceController controller) {
        super(displayInstance, controller);
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
