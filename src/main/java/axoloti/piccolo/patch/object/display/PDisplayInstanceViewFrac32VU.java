package axoloti.piccolo.patch.object.display;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.piccolo.components.displays.PVUComponent;
import java.beans.PropertyChangeEvent;

class PDisplayInstanceViewFrac32VU extends PDisplayInstanceViewFrac32 {

    private final IAxoObjectInstanceView axoObjectInstanceView;

    PDisplayInstanceViewFrac32VU(DisplayInstance displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
	this.axoObjectInstanceView = axoObjectInstanceView;
        initComponents();
    }

    private PVUComponent vu;

    private void initComponents() {
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
