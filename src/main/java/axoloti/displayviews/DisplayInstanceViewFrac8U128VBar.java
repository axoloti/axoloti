package axoloti.displayviews;

import axoloti.displays.DisplayInstance;
import axoloti.displays.DisplayInstanceController;
import axoloti.displays.DisplayInstanceFrac8U128VBar;
import components.VGraphComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewFrac8U128VBar extends DisplayInstanceView {

    private VGraphComponent vgraph;

    public DisplayInstanceViewFrac8U128VBar(DisplayInstanceController controller) {
        super(controller);
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        vgraph = new VGraphComponent(getModel().getN(), 128, 0, 128);
        add(vgraph);
    }

    @Override
    DisplayInstanceFrac8U128VBar getModel() {
        return (DisplayInstanceFrac8U128VBar) super.getModel();
    }

    @Override
    public void updateV() {
        vgraph.setValue(getModel().getIDst());
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
