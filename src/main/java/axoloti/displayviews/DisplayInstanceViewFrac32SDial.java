package axoloti.displayviews;

import axoloti.displays.DisplayInstanceController;
import components.displays.DispComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewFrac32SDial extends DisplayInstanceViewFrac32 {

    private DispComponent dial;

    DisplayInstanceViewFrac32SDial(DisplayInstanceController controller) {
        super(controller);
    }

    @Override
    void PostConstructor() {
        super.PostConstructor();

        dial = new DispComponent(0.0, -64.0, 64.0);
        add(dial);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (evt.getPropertyName().equals(DisplayInstanceController.DISP_VALUE)) {
            dial.setValue((Double) evt.getNewValue());
        }
    }

}
