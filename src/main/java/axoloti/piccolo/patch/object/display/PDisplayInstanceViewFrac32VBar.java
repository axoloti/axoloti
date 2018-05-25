package axoloti.piccolo.patch.object.display;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.piccolo.components.displays.PVBarComponent;
import java.beans.PropertyChangeEvent;

class PDisplayInstanceViewFrac32VBar extends PDisplayInstanceViewFrac32 {

    private PVBarComponent vbar;
    private final IAxoObjectInstanceView axoObjectInstanceView;

    PDisplayInstanceViewFrac32VBar(DisplayInstance displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
	this.axoObjectInstanceView = axoObjectInstanceView;
        initComponents();
    }

    private void initComponents() {
        vbar = new PVBarComponent(0, 0, 64, axoObjectInstanceView);
        vbar.setValue(0);
        addChild(vbar);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (DisplayInstance.DISP_VALUE.is(evt)) {
            vbar.setValue((Double) evt.getNewValue());
        }
    }
}
