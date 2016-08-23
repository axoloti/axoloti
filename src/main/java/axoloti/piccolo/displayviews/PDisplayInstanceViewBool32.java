package axoloti.piccolo.displayviews;

import axoloti.displays.DisplayInstanceBool32;
import axoloti.objectviews.IAxoObjectInstanceView;
import components.piccolo.displays.PLedstripComponent;
import java.awt.Dimension;

public class PDisplayInstanceViewBool32 extends PDisplayInstanceViewInt32 {

    DisplayInstanceBool32 displayInstance;
    private PLedstripComponent readout;

    public PDisplayInstanceViewBool32(DisplayInstanceBool32 displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
        this.displayInstance = displayInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        readout = new PLedstripComponent(0, 1, axoObjectInstanceView);
        addChild(readout);
        readout.setSize(new Dimension(roundUp(readout.getHeight()), 80));
        setSize(getPreferredSize());
    }

    @Override
    public void updateV() {
        readout.setValue(displayInstance.getValueRef().getInt() > 0 ? 1 : 0);
    }
}
