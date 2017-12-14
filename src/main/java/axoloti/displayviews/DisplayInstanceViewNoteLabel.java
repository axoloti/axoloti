package axoloti.displayviews;

import axoloti.displays.DisplayInstance;
import axoloti.displays.DisplayInstanceController;
import axoloti.displays.DisplayInstanceNoteLabel;
import components.LabelComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewNoteLabel extends DisplayInstanceViewFrac32 {

    DisplayInstanceViewNoteLabel(DisplayInstanceController controller) {
        super(controller);
    }

    private LabelComponent readout;

    @Override
    void PostConstructor() {
        super.PostConstructor();

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
