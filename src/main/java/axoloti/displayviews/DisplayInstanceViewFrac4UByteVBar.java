package axoloti.displayviews;

import axoloti.datatypes.Value;
import axoloti.displays.DisplayInstanceFrac4UByteVBar;
import components.displays.VLineComponent;

public class DisplayInstanceViewFrac4UByteVBar extends DisplayInstanceViewFrac32 {

    DisplayInstanceFrac4UByteVBar displayInstance;

    public DisplayInstanceViewFrac4UByteVBar(DisplayInstanceFrac4UByteVBar displayInstance) {
        super(displayInstance);
        this.displayInstance = displayInstance;
    }
    private VLineComponent vbar[];

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        vbar = new VLineComponent[4];
        for (int i = 0; i < 4; i++) {
            vbar[i] = new VLineComponent(0, 0, 64);
            vbar[i].setValue(0);
            add(vbar[i]);
        }
    }
    
    public Value getValue() {
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