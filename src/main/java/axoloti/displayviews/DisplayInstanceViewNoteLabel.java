package axoloti.displayviews;

import axoloti.displays.DisplayInstanceController;
import axoloti.displays.DisplayInstanceNoteLabel;
import components.LabelComponent;

class DisplayInstanceViewNoteLabel extends DisplayInstanceViewFrac32 {

    DisplayInstanceNoteLabel displayInstance;

    public DisplayInstanceViewNoteLabel(DisplayInstanceNoteLabel displayInstance, DisplayInstanceController controller) {
        super(displayInstance, controller);
        this.displayInstance = displayInstance;
    }

    private LabelComponent readout;

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        readout = new LabelComponent("xxxxx");
        add(readout);
        readout.setSize(40, 18);
    }

    @Override
    public void updateV() {
        readout.setText(displayInstance.getConv().ToReal(displayInstance.getValueRef()));
    }
}
