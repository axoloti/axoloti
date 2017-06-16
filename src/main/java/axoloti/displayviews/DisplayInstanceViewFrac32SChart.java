package axoloti.displayviews;

import axoloti.displays.DisplayInstanceController;
import axoloti.displays.DisplayInstanceFrac32SChart;
import components.displays.ScopeComponent;

class DisplayInstanceViewFrac32SChart extends DisplayInstanceViewFrac32 {

    private ScopeComponent scope;

    public DisplayInstanceViewFrac32SChart(DisplayInstanceController controller) {
        super(controller);
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
        scope.setValue(getModel().getValueRef().getDouble());
    }
}
