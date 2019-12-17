package axoloti.piccolo.patch.object.display;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.piccolo.components.displays.PLedstripComponent;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;

class PDisplayInstanceViewBool32 extends PDisplayInstanceViewInt32 {

    private PLedstripComponent readout;
    private final IAxoObjectInstanceView axoObjectInstanceView;

    PDisplayInstanceViewBool32(DisplayInstance displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
        this.axoObjectInstanceView = axoObjectInstanceView;
        initComponents();
    }

    private void initComponents() {
        readout = new PLedstripComponent(0, 1, axoObjectInstanceView);
        addChild(readout);
        readout.setSize(new Dimension(roundUp(readout.getHeight()), 80));
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (DisplayInstance.DISP_VALUE.is(evt)) {
            int i = (Integer) evt.getNewValue();
            readout.setValue(i > 0 ? 1 : 0);
        }
    }
}
