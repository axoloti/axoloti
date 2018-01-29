package axoloti.piccolo.patch.object.display;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.display.DisplayInstanceController;
import axoloti.piccolo.components.displays.PVLineComponentDB;
import java.beans.PropertyChangeEvent;

public class PDisplayInstanceViewFrac4UByteVBarDB extends PDisplayInstanceViewFrac32 {
    private IAxoObjectInstanceView axoObjectInstanceView;

    public PDisplayInstanceViewFrac4UByteVBarDB(DisplayInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
	this.axoObjectInstanceView = axoObjectInstanceView;
    }

    private PVLineComponentDB vbar[];

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        vbar = new PVLineComponentDB[4];
        for (int i = 0; i < 4; i++) {
            vbar[i] = new PVLineComponentDB(0, -60, 10, axoObjectInstanceView);
            vbar[i].setValue(0);
            addChild(vbar[i]);
        }
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (DisplayInstance.DISP_VALUE.is(evt)) {
            int raw = (Integer) evt.getNewValue();
            vbar[0].setValue((byte) ((raw & 0x000000FF)));
            vbar[1].setValue((byte) ((raw & 0x0000FF00) >> 8));
            vbar[2].setValue((byte) ((raw & 0x00FF0000) >> 16));
            vbar[3].setValue((byte) ((raw & 0xFF000000) >> 24));
        }
    }
}
