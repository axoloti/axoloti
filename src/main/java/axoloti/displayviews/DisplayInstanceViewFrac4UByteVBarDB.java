package axoloti.displayviews;

import axoloti.datatypes.Value;
import axoloti.displays.DisplayInstanceFrac4UByteVBarDB;
import components.displays.VLineComponentDB;

public class DisplayInstanceViewFrac4UByteVBarDB extends DisplayInstanceViewFrac32 {

    DisplayInstanceFrac4UByteVBarDB displayInstance;

    public DisplayInstanceViewFrac4UByteVBarDB(DisplayInstanceFrac4UByteVBarDB displayInstance) {
        super(displayInstance);
        this.displayInstance = displayInstance;
    }

    private VLineComponentDB vbar[];

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        vbar = new VLineComponentDB[4];
        for (int i = 0; i < 4; i++) {
            vbar[i] = new VLineComponentDB(0, -60, 10);
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