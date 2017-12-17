package axoloti.piccolo.displayviews;

import axoloti.patch.object.display.DisplayInstanceFrac32SDial;
import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.piccolo.components.displays.PDispComponent;

public class PDisplayInstanceViewFrac32SDial extends PDisplayInstanceViewFrac32 {

    private PDispComponent dial;

    DisplayInstanceFrac32SDial displayInstance;

    public PDisplayInstanceViewFrac32SDial(DisplayInstanceFrac32SDial displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
        this.displayInstance = displayInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        dial = new PDispComponent(0.0, -64.0, 64.0, axoObjectInstanceView);
        addChild(dial);
    }

    @Override
    public void updateV() {
        //dial.setValue(displayInstance.getValueRef().getDouble());
    }
}
