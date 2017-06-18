package axoloti.piccolo.displayviews;

import axoloti.datatypes.Value;
import axoloti.displays.DisplayInstanceFrac4UByteVBarDB;
import axoloti.objectviews.IAxoObjectInstanceView;
import components.piccolo.displays.PVLineComponentDB;

public class PDisplayInstanceViewFrac4UByteVBarDB extends PDisplayInstanceViewFrac32 {

    DisplayInstanceFrac4UByteVBarDB displayInstance;

    public PDisplayInstanceViewFrac4UByteVBarDB(DisplayInstanceFrac4UByteVBarDB displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
        this.displayInstance = displayInstance;
    }

    private PVLineComponentDB vbar[];

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        vbar = new PVLineComponentDB[4];
        for (int i = 0; i < 4; i++) {
            vbar[i] = new PVLineComponentDB(0, -60, 10, axoObjectInstanceView);
            vbar[i].setValue(0);
            addChild(vbar[i]);
        }
    }

    private Value getValue() {
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
