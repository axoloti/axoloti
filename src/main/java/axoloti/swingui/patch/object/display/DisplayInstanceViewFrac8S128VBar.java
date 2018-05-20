package axoloti.swingui.patch.object.display;

import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.display.DisplayInstanceFrac8S128VBar;
import axoloti.swingui.components.VGraphComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewFrac8S128VBar extends DisplayInstanceView {

    private VGraphComponent vgraph;

    DisplayInstanceViewFrac8S128VBar(DisplayInstance displayInstance) {
        super(displayInstance);
        initComponents();
    }

    private void initComponents() {
        vgraph = new VGraphComponent(getDModel().getN(), 128, -64, 64);
        add(vgraph);
    }

    @Override
    public DisplayInstanceFrac8S128VBar getDModel() {
        return (DisplayInstanceFrac8S128VBar) super.getDModel();
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (DisplayInstance.DISP_VALUE.is(evt)) {
            vgraph.setValue(getDModel().getIDst());
        }
    }
}
