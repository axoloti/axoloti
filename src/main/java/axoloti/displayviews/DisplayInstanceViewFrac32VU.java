package axoloti.displayviews;

import axoloti.displays.DisplayInstanceController;
import axoloti.displays.DisplayInstanceFrac32VU;
import components.displays.VUComponent;

class DisplayInstanceViewFrac32VU extends DisplayInstanceViewFrac32 {

    public DisplayInstanceViewFrac32VU(DisplayInstanceController controller) {
        super(controller);
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
        vu.setValue(getModel().getValueRef().getDouble());
    }
}
