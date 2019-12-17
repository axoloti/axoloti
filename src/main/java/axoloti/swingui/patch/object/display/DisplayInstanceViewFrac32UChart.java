package axoloti.swingui.patch.object.display;

import axoloti.patch.object.display.DisplayInstance;
import axoloti.swingui.components.displays.ScopeComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewFrac32UChart extends DisplayInstanceViewFrac32 {

    private ScopeComponent scope;

    DisplayInstanceViewFrac32UChart(DisplayInstance displayInstance) {
        super(displayInstance);
        initComponents();
    }

    private void initComponents() {
        scope = new ScopeComponent(0.0, 64);
        scope.setValue(64.0);
        add(scope);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (DisplayInstance.DISP_VALUE.is(evt)) {
            scope.setValue((Double) evt.getNewValue());
        }
    }

}
