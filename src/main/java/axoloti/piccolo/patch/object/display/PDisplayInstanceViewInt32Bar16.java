package axoloti.piccolo.patch.object.display;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.piccolo.components.displays.PLedstripComponent;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;

class PDisplayInstanceViewInt32Bar16 extends PDisplayInstanceViewInt32 {

    private PLedstripComponent readout;
    private final IAxoObjectInstanceView axoObjectInstanceView;

    PDisplayInstanceViewInt32Bar16(DisplayInstance displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
	this.axoObjectInstanceView = axoObjectInstanceView;
        initComponents();
    }

    private void initComponents() {
        readout = new PLedstripComponent(0, 16, axoObjectInstanceView);
        addChild(readout);
        readout.setSize(new Dimension(roundUp(readout.getHeight()), 80));
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (DisplayInstance.DISP_VALUE.is(evt)) {
            int i = (Integer) evt.getNewValue();
            if ((i >= 0) && (i < 16)) {
                readout.setValue(1 << i);
            } else {
                readout.setValue(0);
            }
        }
    }
}
