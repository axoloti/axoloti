package axoloti.displayviews;

import axoloti.displays.DisplayInstanceNoteLabel;
import components.LabelComponent;

public class DisplayInstanceViewNoteLabel extends DisplayInstanceViewFrac32 {

    DisplayInstanceNoteLabel displayInstance;

    public DisplayInstanceViewNoteLabel(DisplayInstanceNoteLabel displayInstance) {
        super(displayInstance);
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
