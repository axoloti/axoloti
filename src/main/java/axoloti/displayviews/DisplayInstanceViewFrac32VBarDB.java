package axoloti.displayviews;

import axoloti.displays.DisplayInstanceFrac32VBarDB;
import components.displays.VBarComponentDB;

public class DisplayInstanceViewFrac32VBarDB extends DisplayInstanceViewFrac32 {

    DisplayInstanceFrac32VBarDB displayInstance;

    public DisplayInstanceViewFrac32VBarDB(DisplayInstanceFrac32VBarDB displayInstance) {
        super(displayInstance);
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