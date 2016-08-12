package axoloti.displayviews;

import axoloti.displays.DisplayInstanceFrac32UChart;
import components.displays.ScopeComponent;

public class DisplayInstanceViewFrac32UChart extends DisplayInstanceViewFrac32 {

    DisplayInstanceFrac32UChart displayInstance;
    private ScopeComponent scope;

    public DisplayInstanceViewFrac32UChart(DisplayInstanceFrac32UChart displayInstance) {
        super(displayInstance);
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