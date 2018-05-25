package axoloti.piccolo.patch.object.display;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.piccolo.components.displays.PVBarComponentDB;
import java.beans.PropertyChangeEvent;

class PDisplayInstanceViewFrac32VBarDB extends PDisplayInstanceViewFrac32 {

    private final IAxoObjectInstanceView axoObjectInstanceView;

    PDisplayInstanceViewFrac32VBarDB(DisplayInstance displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
	this.axoObjectInstanceView = axoObjectInstanceView;
        initComponents();
    }

    private PVBarComponentDB vbar;

    private void initComponents() {
        vbar = new PVBarComponentDB(-200, -60, 10, axoObjectInstanceView);
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
