package axoloti.piccolo.displayviews;

import axoloti.datatypes.Value;
import axoloti.displays.DisplayInstanceFrac4ByteVBar;
import axoloti.objectviews.IAxoObjectInstanceView;
import components.piccolo.displays.PVLineComponent;

public class PDisplayInstanceViewFrac4ByteVBar extends PDisplayInstanceViewFrac32 {

    DisplayInstanceFrac4ByteVBar displayInstance;
    private PVLineComponent vbar[];

    public PDisplayInstanceViewFrac4ByteVBar(DisplayInstanceFrac4ByteVBar displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
        this.displayInstance = displayInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        vbar = new PVLineComponent[4];
        for (int i = 0; i < 4; i++) {
            vbar[i] = new PVLineComponent(0, -64, 64, axoObjectInstanceView);
            vbar[i].setValue(0);
            addChild(vbar[i]);
        }
    }

    private Value getValue() {
        return displayInstance.getValueRef();
    }

    @Override
    public void updateV() {
        vbar[0].setValue((byte) ((getValue().getRaw() & 0x000000FF)));
        vbar[1].setValue((byte) ((getValue().getRaw() & 0x0000FF00) >> 8));
        vbar[2].setValue((byte) ((getValue().getRaw() & 0x00FF0000) >> 16));
        vbar[3].setValue((byte) ((getValue().getRaw() & 0xFF000000) >> 24));
    }
}
