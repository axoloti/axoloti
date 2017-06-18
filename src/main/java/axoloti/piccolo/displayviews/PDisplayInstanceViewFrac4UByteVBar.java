package axoloti.piccolo.displayviews;

import axoloti.datatypes.Value;
import axoloti.displays.DisplayInstanceFrac4UByteVBar;
import axoloti.objectviews.IAxoObjectInstanceView;
import components.piccolo.displays.PVLineComponent;

public class PDisplayInstanceViewFrac4UByteVBar extends PDisplayInstanceViewFrac32 {

    DisplayInstanceFrac4UByteVBar displayInstance;

    public PDisplayInstanceViewFrac4UByteVBar(DisplayInstanceFrac4UByteVBar displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
        this.displayInstance = displayInstance;
    }
    private PVLineComponent vbar[];

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        vbar = new PVLineComponent[4];
        for (int i = 0; i < 4; i++) {
            vbar[i] = new PVLineComponent(0, 0, 64, axoObjectInstanceView);
            vbar[i].setValue(0);
            addChild(vbar[i]);
        }
    }

    public Value getValue() {
        //return displayInstance.getValueRef();
        return null;
    }

    @Override
    public void updateV() {
        vbar[0].setValue((byte) ((getValue().getRaw() & 0x000000FF)));
        vbar[1].setValue((byte) ((getValue().getRaw() & 0x0000FF00) >> 8));
        vbar[2].setValue((byte) ((getValue().getRaw() & 0x00FF0000) >> 16));
        vbar[3].setValue((byte) ((getValue().getRaw() & 0xFF000000) >> 24));
    }
}
