package axoloti.displayviews;

import axoloti.displays.DisplayInstanceController;
import axoloti.displays.DisplayInstanceInt32Label;
import components.LabelComponent;

class DisplayInstanceViewInt32Label extends DisplayInstanceViewInt32 {

    private LabelComponent readout;

    public DisplayInstanceViewInt32Label(DisplayInstanceController controller) {
        super(controller);
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
        readout.setText(":" + Integer.toString(getModel().getValueRef().getInt()));
    }
}
