package axoloti.displayviews;

import axoloti.displays.DisplayInstanceFrac32UDial;
import components.displays.DispComponent;

public class DisplayInstanceViewFrac32UDial extends DisplayInstanceViewFrac32 {

    private DispComponent dial;

    DisplayInstanceFrac32UDial displayInstance;

    public DisplayInstanceViewFrac32UDial(DisplayInstanceFrac32UDial displayInstance) {
        super(displayInstance);
        this.displayInstance = displayInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        dial = new DispComponent(0.0, 0.0, 64.0);
        add(dial);
    }

    @Override
    public void updateV() {
        dial.setValue(displayInstance.getValueRef().getDouble());
    }
}