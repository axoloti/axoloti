package axoloti.displayviews;

import axoloti.displays.DisplayInstance;
import axoloti.displays.DisplayInstanceController;
import components.displays.DispComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewFrac32UDial extends DisplayInstanceViewFrac32 {

    private DispComponent dial;

    DisplayInstanceViewFrac32UDial(DisplayInstanceController controller) {
        super(controller);
    }

    @Override
    void PostConstructor() {
        super.PostConstructor();

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
