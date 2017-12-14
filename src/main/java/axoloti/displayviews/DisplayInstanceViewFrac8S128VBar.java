package axoloti.displayviews;

import axoloti.displays.DisplayInstance;
import axoloti.displays.DisplayInstanceController;
import axoloti.displays.DisplayInstanceFrac8S128VBar;
import components.VGraphComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewFrac8S128VBar extends DisplayInstanceView {

    private VGraphComponent vgraph;

    DisplayInstanceViewFrac8S128VBar(DisplayInstanceController controller) {
        super(controller);
    }

    @Override
    void PostConstructor() {
        super.PostConstructor();
        vgraph = new VGraphComponent(getModel().getN(), 128, -64, 64);
        add(vgraph);
    }

    @Override
    DisplayInstanceFrac8S128VBar getModel() {
        return (DisplayInstanceFrac8S128VBar) super.getModel();
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (DisplayInstance.DISP_VALUE.is(evt)) {
            vgraph.setValue(getModel().getIDst());
        }
    }
}
