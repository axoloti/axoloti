package axoloti.displayviews;

import axoloti.displays.DisplayInstanceController;
import axoloti.displays.DisplayInstanceFrac8S128VBar;
import components.VGraphComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewFrac8S128VBar extends DisplayInstanceView {

    private VGraphComponent vgraph;

    public DisplayInstanceViewFrac8S128VBar(DisplayInstanceController controller) {
        super(controller);
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        vgraph = new VGraphComponent(getModel().getN(), 128, -64, 64);
        add(vgraph);
    }

    @Override
    DisplayInstanceFrac8S128VBar getModel() {
        return (DisplayInstanceFrac8S128VBar) super.getModel();
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
