package axoloti.piccolo.displayviews;

import axoloti.patch.object.display.DisplayInstanceFrac32VBar;
import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.piccolo.components.displays.PVBarComponent;

public class PDisplayInstanceViewFrac32VBar extends PDisplayInstanceViewFrac32 {

    DisplayInstanceFrac32VBar displayInstance;
    private PVBarComponent vbar;

    public PDisplayInstanceViewFrac32VBar(DisplayInstanceFrac32VBar displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
        this.displayInstance = displayInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        vbar = new PVBarComponent(0, 0, 64, axoObjectInstanceView);
        vbar.setValue(0);
        addChild(vbar);
    }

    @Override
    public void updateV() {
        //vbar.setValue(displayInstance.getValueRef().getDouble());
    }
}
