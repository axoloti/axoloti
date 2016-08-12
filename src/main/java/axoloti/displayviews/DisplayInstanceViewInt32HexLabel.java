package axoloti.displayviews;

import axoloti.displays.DisplayInstanceInt32HexLabel;
import components.LabelComponent;

public class DisplayInstanceViewInt32HexLabel extends DisplayInstanceViewInt32 {

    private DisplayInstanceInt32HexLabel displayInstance;
    private LabelComponent readout;

    public DisplayInstanceViewInt32HexLabel(DisplayInstanceInt32HexLabel displayInstance) {
        super(displayInstance);
        this.displayInstance = displayInstance;
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
        readout.setText(String.format("0x%08X", displayInstance.getValueRef().getInt()));
    }
}