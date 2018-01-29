package axoloti.piccolo.patch.object.display;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.display.DisplayInstanceController;
import axoloti.piccolo.components.PLabelComponent;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;

public class PDisplayInstanceViewInt32Label extends PDisplayInstanceViewInt32 {
    private PLabelComponent readout;

    public PDisplayInstanceViewInt32Label(DisplayInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        readout = new PLabelComponent("xxxxxx");
        addChild(readout);
        readout.setSize(new Dimension(80, 18));
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (DisplayInstance.DISP_VALUE.is(evt)) {
            int i = (Integer) evt.getNewValue();
            readout.setText(":" + Integer.toString(i));
        }
    }
}
