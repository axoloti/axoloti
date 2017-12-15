package axoloti.piccolo.patch.object.display;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.display.DisplayInstanceController;
import axoloti.piccolo.components.PLabelComponent;

public class PDisplayInstanceViewNoteLabel extends PDisplayInstanceViewFrac32 {

    public PDisplayInstanceViewNoteLabel(DisplayInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
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
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (DisplayInstance.DISP_VALUE.is(evt)) {
            throw new UnsupportedOperationException("Not supported yet.");
            //readout.setText(getModel().getConv().ToReal(((Value) evt.getNewValue())));
        }
    }
}
