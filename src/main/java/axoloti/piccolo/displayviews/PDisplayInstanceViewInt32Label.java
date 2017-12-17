package axoloti.piccolo.displayviews;

import axoloti.patch.object.display.DisplayInstanceInt32Label;
import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.piccolo.components.PLabelComponent;
import java.awt.Dimension;

public class PDisplayInstanceViewInt32Label extends PDisplayInstanceViewInt32 {

    private DisplayInstanceInt32Label displayInstance;
    private PLabelComponent readout;

    public PDisplayInstanceViewInt32Label(DisplayInstanceInt32Label displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
        this.displayInstance = displayInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        readout = new PLabelComponent("xxxxxx");
        addChild(readout);
        readout.setSize(new Dimension(80, 18));
    }

    @Override
    public void updateV() {
        //readout.setText(Integer.toString(displayInstance.getValueRef().getInt()));
    }
}
