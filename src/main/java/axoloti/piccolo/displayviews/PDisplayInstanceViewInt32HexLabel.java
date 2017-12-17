package axoloti.piccolo.displayviews;

import axoloti.patch.object.display.DisplayInstanceInt32HexLabel;
import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.piccolo.components.PLabelComponent;
import java.awt.Dimension;

public class PDisplayInstanceViewInt32HexLabel extends PDisplayInstanceViewInt32 {

    private DisplayInstanceInt32HexLabel displayInstance;
    private PLabelComponent readout;

    public PDisplayInstanceViewInt32HexLabel(DisplayInstanceInt32HexLabel displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
        this.displayInstance = displayInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        readout = new PLabelComponent("0xxxxxxxxx");
        addChild(readout);
        readout.setSize(new Dimension(80, 18));
    }

    @Override
    public void updateV() {
        //readout.setText(String.format("0x%08X", displayInstance.getValueRef().getInt()));
    }
}
