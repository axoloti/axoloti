package axoloti.displayviews;

import axoloti.displays.DisplayInstanceController;
import axoloti.displays.DisplayInstanceFrac32VBar;
import components.displays.VBarComponent;

class DisplayInstanceViewFrac32VBar extends DisplayInstanceViewFrac32 {

    private VBarComponent vbar;

    public DisplayInstanceViewFrac32VBar(DisplayInstanceController controller) {
        super(controller);
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        vbar = new VBarComponent(0, 0, 64);
        vbar.setValue(0);
        add(vbar);
    }

    @Override
    public void updateV() {
        vbar.setValue(getModel().getValueRef().getDouble());
    }
}
