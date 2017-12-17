package axoloti.swingui.patch.object.display;

import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.display.DisplayInstanceController;
import axoloti.patch.object.display.DisplayInstanceFrac8S128VBar;
import axoloti.swingui.components.VGraphComponent;
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
