package axoloti.swingui.patch.object.display;

import axoloti.patch.object.display.DisplayInstance;
import axoloti.swingui.components.displays.VBarComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewFrac32VBar extends DisplayInstanceViewFrac32 {

    private VBarComponent vbar;

    DisplayInstanceViewFrac32VBar(DisplayInstance displayInstance) {
        super(displayInstance);
        initComponents();
    }

    private void initComponents() {
        vbar = new VBarComponent(0, 0, 64);
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
