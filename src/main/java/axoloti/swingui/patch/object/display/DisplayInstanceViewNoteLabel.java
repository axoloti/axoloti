package axoloti.swingui.patch.object.display;

import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.display.DisplayInstanceController;
import axoloti.patch.object.display.DisplayInstanceNoteLabel;
import axoloti.swingui.components.LabelComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewNoteLabel extends DisplayInstanceViewFrac32 {

    private LabelComponent readout;

    DisplayInstanceViewNoteLabel(DisplayInstanceController controller) {
        super(controller);
        initComponents();
    }

    private void initComponents() {
        readout = new LabelComponent("xxxxx");
        add(readout);
        readout.setSize(40, 18);
    }

    @Override
    DisplayInstanceNoteLabel getModel() {
        return (DisplayInstanceNoteLabel) super.getModel();
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
