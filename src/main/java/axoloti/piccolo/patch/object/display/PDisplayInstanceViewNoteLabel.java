package axoloti.piccolo.patch.object.display;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.piccolo.components.PLabelComponent;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;

class PDisplayInstanceViewNoteLabel extends PDisplayInstanceViewFrac32 {

    PDisplayInstanceViewNoteLabel(DisplayInstance displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
        initComponents();
    }

    private PLabelComponent readout;

    private void initComponents() {
        readout = new PLabelComponent("xxxxx");
        addChild(readout);
        readout.setSize(new Dimension(40, 18));
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (DisplayInstance.DISP_VALUE.is(evt)) {
            throw new UnsupportedOperationException("Not supported yet.");
            //readout.setText(getModel().getConv().ToReal(((Value) evt.getNewValue())));
        }
    }
}
