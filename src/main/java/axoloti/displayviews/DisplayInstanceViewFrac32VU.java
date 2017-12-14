package axoloti.displayviews;

import axoloti.displays.DisplayInstance;
import axoloti.displays.DisplayInstanceController;
import components.displays.VUComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewFrac32VU extends DisplayInstanceViewFrac32 {

    DisplayInstanceViewFrac32VU(DisplayInstanceController controller) {
        super(controller);
    }

    private VUComponent vu;

    @Override
    void PostConstructor() {
        super.PostConstructor();

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
