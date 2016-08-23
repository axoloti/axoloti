package axoloti.piccolo.displayviews;

import axoloti.displays.DisplayInstanceFrac32UChart;
import axoloti.objectviews.IAxoObjectInstanceView;
import components.piccolo.displays.PScopeComponent;

public class PDisplayInstanceViewFrac32UChart extends PDisplayInstanceViewFrac32 {

    DisplayInstanceFrac32UChart displayInstance;
    private PScopeComponent scope;

    public PDisplayInstanceViewFrac32UChart(DisplayInstanceFrac32UChart displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
        this.displayInstance = displayInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        scope = new PScopeComponent(0.0, 64, axoObjectInstanceView);
        scope.setValue(64.0);
        addChild(scope);
    }

    @Override
    public void updateV() {
        scope.setValue(displayInstance.getValueRef().getDouble());
    }
}
