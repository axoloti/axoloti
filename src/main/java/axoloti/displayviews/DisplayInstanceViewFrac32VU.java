package axoloti.displayviews;

import axoloti.displays.DisplayInstanceFrac32VU;
import components.displays.VUComponent;

public class DisplayInstanceViewFrac32VU extends DisplayInstanceViewFrac32 {

    DisplayInstanceFrac32VU displayInstance;

    public DisplayInstanceViewFrac32VU(DisplayInstanceFrac32VU displayInstance) {
        super(displayInstance);
        this.displayInstance = displayInstance;
    }

    private VUComponent vu;

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        vu = new VUComponent();
        vu.setValue(0);
        add(vu);
    }

    @Override
    public void updateV() {
        vu.setValue(displayInstance.getValueRef().getDouble());
    }
}