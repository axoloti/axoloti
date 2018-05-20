package axoloti.swingui.patch.object.display;

import axoloti.patch.object.display.DisplayInstance;
import axoloti.swingui.components.displays.DispComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewFrac32UDial extends DisplayInstanceViewFrac32 {

    private DispComponent dial;

    DisplayInstanceViewFrac32UDial(DisplayInstance displayInstance) {
        super(displayInstance);
        initComponents();
    }

    private void initComponents() {
        dial = new DispComponent(0.0, 0.0, 64.0);
        add(dial);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (DisplayInstance.DISP_VALUE.is(evt)) {
            dial.setValue((Double) evt.getNewValue());
        }
    }
}
