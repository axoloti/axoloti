package axoloti.displayviews;

import axoloti.displays.DisplayInstanceController;
import axoloti.displays.DisplayInstanceFrac32UChart;
import components.displays.ScopeComponent;

class DisplayInstanceViewFrac32UChart extends DisplayInstanceViewFrac32 {

    DisplayInstanceFrac32UChart displayInstance;
    private ScopeComponent scope;

    public DisplayInstanceViewFrac32UChart(DisplayInstanceFrac32UChart displayInstance, DisplayInstanceController controller) {
        super(displayInstance, controller);
        this.displayInstance = displayInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        scope = new ScopeComponent(0.0, 64);
        scope.setValue(64.0);
        add(scope);
    }

    @Override
    public void updateV() {
        scope.setValue(displayInstance.getValueRef().getDouble());
    }
}