package axoloti.piccolo.patch.object.display;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.display.DisplayInstanceController;
import axoloti.piccolo.components.displays.PVUComponent;
import java.beans.PropertyChangeEvent;

public class PDisplayInstanceViewFrac32VU extends PDisplayInstanceViewFrac32 {
    private IAxoObjectInstanceView axoObjectInstanceView;

    public PDisplayInstanceViewFrac32VU(DisplayInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
	this.axoObjectInstanceView = axoObjectInstanceView;
    }

    private PVUComponent vu;

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        vu = new PVUComponent(axoObjectInstanceView);
        vu.setValue(0);
        addChild(vu);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (DisplayInstance.DISP_VALUE.is(evt)) {
            vu.setValue((Double) evt.getNewValue());
        }
    }
}
