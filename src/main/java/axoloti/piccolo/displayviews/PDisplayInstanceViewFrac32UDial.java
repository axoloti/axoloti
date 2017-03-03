package axoloti.piccolo.displayviews;

import axoloti.displays.DisplayInstanceFrac32UDial;
import axoloti.objectviews.IAxoObjectInstanceView;
import components.piccolo.displays.PDispComponent;

public class PDisplayInstanceViewFrac32UDial extends PDisplayInstanceViewFrac32 {

    private PDispComponent dial;

    DisplayInstanceFrac32UDial displayInstance;

    public PDisplayInstanceViewFrac32UDial(DisplayInstanceFrac32UDial displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
        this.displayInstance = displayInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        dial = new PDispComponent(0.0, 0.0, 64.0, axoObjectInstanceView);
        addChild(dial);
    }

    @Override
    public void updateV() {
        dial.setValue(displayInstance.getValueRef().getDouble());
    }
}
