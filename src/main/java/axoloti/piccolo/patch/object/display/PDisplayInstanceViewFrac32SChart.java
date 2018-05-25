package axoloti.piccolo.patch.object.display;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.piccolo.components.displays.PScopeComponent;
import java.beans.PropertyChangeEvent;

class PDisplayInstanceViewFrac32SChart extends PDisplayInstanceViewFrac32 {

    private PScopeComponent scope;
    private final IAxoObjectInstanceView axoObjectInstanceView;

    PDisplayInstanceViewFrac32SChart(DisplayInstance displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
        this.axoObjectInstanceView = axoObjectInstanceView;
        initComponents();
    }

    private void initComponents() {
        scope = new PScopeComponent(-64.0, 64.0, axoObjectInstanceView);
        scope.setValue(0);
        addChild(scope);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (DisplayInstance.DISP_VALUE.is(evt)) {
            scope.setValue((Double) evt.getNewValue());
        }
    }

}
