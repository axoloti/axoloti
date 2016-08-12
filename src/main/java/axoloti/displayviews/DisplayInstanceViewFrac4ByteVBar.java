package axoloti.displayviews;

import axoloti.datatypes.Value;
import axoloti.displays.DisplayInstanceFrac4ByteVBar;
import components.displays.VLineComponent;

public class DisplayInstanceViewFrac4ByteVBar extends DisplayInstanceViewFrac32 {

    DisplayInstanceFrac4ByteVBar displayInstance;
    private VLineComponent vbar[];

    public DisplayInstanceViewFrac4ByteVBar(DisplayInstanceFrac4ByteVBar displayInstance) {
        super(displayInstance);
        this.displayInstance = displayInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        vbar = new VLineComponent[4];
        for (int i = 0; i < 4; i++) {
            vbar[i] = new VLineComponent(0, -64, 64);
            vbar[i].setValue(0);
            add(vbar[i]);
        }
    }

    private Value getValue() {
        return this.displayInstance.getValueRef();
    }

    @Override
    public void updateV() {
        vbar[0].setValue((byte) ((getValue().getRaw() & 0x000000FF)));
        vbar[1].setValue((byte) ((getValue().getRaw() & 0x0000FF00) >> 8));
        vbar[2].setValue((byte) ((getValue().getRaw() & 0x00FF0000) >> 16));
        vbar[3].setValue((byte) ((getValue().getRaw() & 0xFF000000) >> 24));
    }
}
