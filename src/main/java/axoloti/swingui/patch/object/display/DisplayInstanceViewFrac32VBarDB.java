package axoloti.swingui.patch.object.display;

import axoloti.patch.object.display.DisplayInstance;
import axoloti.swingui.components.displays.VBarComponentDB;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewFrac32VBarDB extends DisplayInstanceViewFrac32 {

    private VBarComponentDB vbar;

    DisplayInstanceViewFrac32VBarDB(DisplayInstance displayInstance) {
        super(displayInstance);
        initComponents();
    }

    private void initComponents() {
        vbar = new VBarComponentDB(-200, -60, 10);
        vbar.setValue(0);
        add(vbar);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (DisplayInstance.DISP_VALUE.is(evt)) {
            vbar.setValue((Double) evt.getNewValue());
        }
    }
}
