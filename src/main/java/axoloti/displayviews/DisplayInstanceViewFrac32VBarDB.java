package axoloti.displayviews;

import axoloti.displays.DisplayInstanceController;
import axoloti.displays.DisplayInstanceFrac32VBarDB;
import components.displays.VBarComponentDB;

class DisplayInstanceViewFrac32VBarDB extends DisplayInstanceViewFrac32 {

    DisplayInstanceFrac32VBarDB displayInstance;

    public DisplayInstanceViewFrac32VBarDB(DisplayInstanceFrac32VBarDB displayInstance, DisplayInstanceController controller) {
        super(displayInstance, controller);
        this.displayInstance = displayInstance;
    }

    private VBarComponentDB vbar;

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        vbar = new VBarComponentDB(-200, -60, 10);
        vbar.setValue(0);
        add(vbar);
    }

    @Override
    public void updateV() {
        vbar.setValue(displayInstance.getValueRef().getDouble());
    }
}