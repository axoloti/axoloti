package axoloti.displayviews;

import axoloti.displays.DisplayInstanceFrac32SChart;
import components.displays.ScopeComponent;

public class DisplayInstanceViewFrac32SChart extends DisplayInstanceViewFrac32 {

    DisplayInstanceFrac32SChart displayInstance;
    private ScopeComponent scope;

    public DisplayInstanceViewFrac32SChart(DisplayInstanceFrac32SChart displayInstance) {
        super(displayInstance);
        this.displayInstance = displayInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        scope = new ScopeComponent(-64.0, 64.0);
        scope.setValue(0);
        add(scope);
    }

    @Override
    public void updateV() {
        scope.setValue(displayInstance.getValueRef().getDouble());
    }
}
