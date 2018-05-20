package axoloti.swingui.patch.object.display;

import axoloti.patch.object.display.DisplayInstance;
import axoloti.swingui.components.displays.VUComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewFrac32VU extends DisplayInstanceViewFrac32 {

    private VUComponent vu;

    DisplayInstanceViewFrac32VU(DisplayInstance displayInstance) {
        super(displayInstance);
        initComponents();
    }

    private void initComponents() {
        vu = new VUComponent();
        vu.setValue(0);
        add(vu);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (DisplayInstance.DISP_VALUE.is(evt)) {
            vu.setValue((Double) evt.getNewValue());
        }
    }
}
