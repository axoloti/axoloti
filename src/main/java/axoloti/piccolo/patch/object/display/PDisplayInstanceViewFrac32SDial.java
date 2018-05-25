package axoloti.piccolo.patch.object.display;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.piccolo.components.displays.PDispComponent;
import java.beans.PropertyChangeEvent;

class PDisplayInstanceViewFrac32SDial extends PDisplayInstanceViewFrac32 {

    private PDispComponent dial;
    private final IAxoObjectInstanceView axoObjectInstanceView;

    PDisplayInstanceViewFrac32SDial(DisplayInstance displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
        this.axoObjectInstanceView = axoObjectInstanceView;
        initComponents();
    }

    private void initComponents() {
        dial = new PDispComponent(0.0, -64.0, 64.0, axoObjectInstanceView);
        addChild(dial);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (DisplayInstance.DISP_VALUE.is(evt)) {
            dial.setValue((Double) evt.getNewValue());
        }
    }
}
