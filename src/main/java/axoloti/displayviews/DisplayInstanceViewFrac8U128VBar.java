package axoloti.displayviews;

import axoloti.displays.DisplayInstanceFrac8U128VBar;
import components.VGraphComponent;

public class DisplayInstanceViewFrac8U128VBar extends DisplayInstanceView {

    DisplayInstanceFrac8U128VBar displayInstance;
    private VGraphComponent vgraph;

    public DisplayInstanceViewFrac8U128VBar(DisplayInstanceFrac8U128VBar displayInstance) {
        super(displayInstance);
        this.displayInstance = displayInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        vgraph = new VGraphComponent(displayInstance.getN(), 128, 0, 128);
        add(vgraph);
    }

    @Override
    public void updateV() {
        vgraph.setValue(displayInstance.getIDst());
    }
}