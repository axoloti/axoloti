package axoloti.swingui.patch.object.display;

import axoloti.patch.object.display.DisplayInstance;
import axoloti.swingui.components.displays.VLineComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewFrac4UByteVBar extends DisplayInstanceViewFrac32 {

    private VLineComponent vbar[];

    DisplayInstanceViewFrac4UByteVBar(DisplayInstance displayInstance) {
        super(displayInstance);
        initComponents();
    }

    private void initComponents() {
        vbar = new VLineComponent[4];
        for (int i = 0; i < 4; i++) {
            vbar[i] = new VLineComponent(0, 0, 64);
            vbar[i].setValue(0);
            add(vbar[i]);
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
