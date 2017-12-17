package axoloti.piccolo.displayviews;

import axoloti.patch.object.display.DisplayInstanceInt32Bar16;
import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.piccolo.components.displays.PLedstripComponent;
import java.awt.Dimension;

public class PDisplayInstanceViewInt32Bar16 extends PDisplayInstanceViewInt32 {

    private DisplayInstanceInt32Bar16 displayInstance;
    private PLedstripComponent readout;

    public PDisplayInstanceViewInt32Bar16(DisplayInstanceInt32Bar16 displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
        this.displayInstance = displayInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        readout = new PLedstripComponent(0, 16, axoObjectInstanceView);
        addChild(readout);
        readout.setSize(new Dimension(roundUp(readout.getHeight()), 80));
    }

    @Override
    public void updateV() {
        int i = 0;//displayInstance.getValueRef().getInt();
        if ((i >= 0) && (i < 16)) {
            readout.setValue(1 << i);
        } else {
            readout.setValue(0);
        }
    }
}
