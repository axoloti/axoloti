package axoloti.displayviews;

import axoloti.displays.DisplayInstanceController;
import axoloti.displays.DisplayInstanceFrac32SDial;
import components.displays.DispComponent;

class DisplayInstanceViewFrac32SDial extends DisplayInstanceViewFrac32 {

    private DispComponent dial;

    public DisplayInstanceViewFrac32SDial(DisplayInstanceController controller) {
        super(controller);
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        dial = new DispComponent(0.0, -64.0, 64.0);
        add(dial);
    }

    @Override
    public void updateV() {
        dial.setValue(getModel().getValueRef().getDouble());
    }
}
