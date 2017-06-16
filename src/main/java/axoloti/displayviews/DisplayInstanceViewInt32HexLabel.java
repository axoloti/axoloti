package axoloti.displayviews;

import axoloti.displays.DisplayInstanceController;
import axoloti.displays.DisplayInstanceInt32HexLabel;
import components.LabelComponent;

class DisplayInstanceViewInt32HexLabel extends DisplayInstanceViewInt32 {

    private LabelComponent readout;

    public DisplayInstanceViewInt32HexLabel(DisplayInstanceController controller) {
        super(controller);
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        readout = new LabelComponent("0xxxxxxxxx");
        add(readout);
        readout.setSize(80, 18);
    }

    @Override
    public void updateV() {
        readout.setText(String.format("0x%08X", getModel().getValueRef().getInt()));
    }
}
