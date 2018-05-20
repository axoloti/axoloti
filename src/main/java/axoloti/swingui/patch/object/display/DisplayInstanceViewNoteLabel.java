package axoloti.swingui.patch.object.display;

import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.display.DisplayInstanceNoteLabel;
import axoloti.swingui.components.LabelComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewNoteLabel extends DisplayInstanceViewFrac32 {

    private LabelComponent readout;

    DisplayInstanceViewNoteLabel(DisplayInstance displayInstance) {
        super(displayInstance);
        initComponents();
    }

    private void initComponents() {
        readout = new LabelComponent("xxxxx");
        add(readout);
        readout.setSize(40, 18);
    }

    @Override
    public DisplayInstanceNoteLabel getDModel() {
        return (DisplayInstanceNoteLabel) super.getDModel();
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
