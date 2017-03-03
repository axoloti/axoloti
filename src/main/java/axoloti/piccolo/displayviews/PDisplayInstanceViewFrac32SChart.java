package axoloti.piccolo.displayviews;

import axoloti.displays.DisplayInstanceFrac32SChart;
import axoloti.objectviews.IAxoObjectInstanceView;
import components.piccolo.displays.PScopeComponent;

public class PDisplayInstanceViewFrac32SChart extends PDisplayInstanceViewFrac32 {

    DisplayInstanceFrac32SChart displayInstance;
    private PScopeComponent scope;

    public PDisplayInstanceViewFrac32SChart(DisplayInstanceFrac32SChart displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
        this.displayInstance = displayInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        scope = new PScopeComponent(-64.0, 64.0, axoObjectInstanceView);
        scope.setValue(0);
        addChild(scope);
    }

    @Override
    public void updateV() {
        scope.setValue(displayInstance.getValueRef().getDouble());
    }
}
