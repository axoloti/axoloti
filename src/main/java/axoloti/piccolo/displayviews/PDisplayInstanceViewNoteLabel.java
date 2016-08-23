package axoloti.piccolo.displayviews;

import axoloti.displays.DisplayInstanceNoteLabel;
import axoloti.objectviews.IAxoObjectInstanceView;
import components.piccolo.PLabelComponent;
import java.awt.Dimension;

public class PDisplayInstanceViewNoteLabel extends PDisplayInstanceViewFrac32 {

    DisplayInstanceNoteLabel displayInstance;

    public PDisplayInstanceViewNoteLabel(DisplayInstanceNoteLabel displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
        this.displayInstance = displayInstance;
    }

    private PLabelComponent readout;

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        readout = new PLabelComponent("xxxxx");
        addChild(readout);
        readout.setSize(new Dimension(40, 18));
    }

    @Override
    public void updateV() {
        readout.setText(displayInstance.getConv().ToReal(displayInstance.getValueRef()));
    }
}
