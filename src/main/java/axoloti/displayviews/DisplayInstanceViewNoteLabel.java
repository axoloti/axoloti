package axoloti.displayviews;

import axoloti.displays.DisplayInstanceController;
import axoloti.displays.DisplayInstanceFrac32;
import axoloti.displays.DisplayInstanceNoteLabel;
import components.LabelComponent;

class DisplayInstanceViewNoteLabel extends DisplayInstanceViewFrac32 {

    public DisplayInstanceViewNoteLabel(DisplayInstanceController controller) {
        super(controller);
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
    DisplayInstanceNoteLabel getModel() {
        return (DisplayInstanceNoteLabel) super.getModel();
    }

    @Override
    public void updateV() {
        readout.setText(getModel().getConv().ToReal(getModel().getValueRef()));
    }
}
