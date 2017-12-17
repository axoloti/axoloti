package axoloti.swingui.patch.object.display;

import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.display.DisplayInstanceController;
import axoloti.patch.object.display.DisplayInstanceFrac8U128VBar;
import axoloti.swingui.components.VGraphComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewFrac8U128VBar extends DisplayInstanceView {

    private VGraphComponent vgraph;

    DisplayInstanceViewFrac8U128VBar(DisplayInstanceController controller) {
        super(controller);
    }

    @Override
    void PostConstructor() {
        super.PostConstructor();
        vgraph = new VGraphComponent(getModel().getN(), 128, 0, 128);
        add(vgraph);
    }

    @Override
    DisplayInstanceFrac8U128VBar getModel() {
        return (DisplayInstanceFrac8U128VBar) super.getModel();
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (DisplayInstance.DISP_VALUE.is(evt)) {
            vgraph.setValue(getModel().getIDst());
        }
    }
}
