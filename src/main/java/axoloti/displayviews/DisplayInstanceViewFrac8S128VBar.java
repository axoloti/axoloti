package axoloti.displayviews;

import axoloti.displays.DisplayInstanceController;
import axoloti.displays.DisplayInstanceFrac8S128VBar;
import components.VGraphComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewFrac8S128VBar extends DisplayInstanceView {

    DisplayInstanceFrac8S128VBar displayInstance;
    private VGraphComponent vgraph;

    public DisplayInstanceViewFrac8S128VBar(DisplayInstanceFrac8S128VBar displayInstance, DisplayInstanceController controller) {
        super(displayInstance, controller);
        this.displayInstance = displayInstance;
    }
    
    @Override
    public void PostConstructor() {
        super.PostConstructor();
        vgraph = new VGraphComponent(displayInstance.getN(), 128, -64, 64);
        add(vgraph);
    }
    
    @Override
    public void updateV() {
        vgraph.setValue(displayInstance.getIDst());
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        throw new UnsupportedOperationException("Not supported yet.");
    }
}