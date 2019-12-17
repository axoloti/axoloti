package axoloti.piccolo.patch.object.display;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.piccolo.components.PLabelComponent;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;

class PDisplayInstanceViewInt32HexLabel extends PDisplayInstanceViewInt32 {

    private PLabelComponent readout;

    PDisplayInstanceViewInt32HexLabel(DisplayInstance displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
        initComponents();
    }

    private void initComponents() {
        readout = new PLabelComponent("0xxxxxxxxx");
        addChild(readout);
        readout.setSize(new Dimension(80, 18));
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (DisplayInstance.DISP_VALUE.is(evt)) {
            int i = (Integer) evt.getNewValue();
            readout.setText(String.format("0x%08X", i));
        }
    }
}
