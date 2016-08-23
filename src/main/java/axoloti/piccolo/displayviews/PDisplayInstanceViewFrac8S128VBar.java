package axoloti.piccolo.displayviews;

import axoloti.displays.DisplayInstanceFrac8S128VBar;
import axoloti.objectviews.IAxoObjectInstanceView;
import components.piccolo.PVGraphComponent;

public class PDisplayInstanceViewFrac8S128VBar extends PDisplayInstanceView {

    DisplayInstanceFrac8S128VBar displayInstance;
    private PVGraphComponent vgraph;

    public PDisplayInstanceViewFrac8S128VBar(DisplayInstanceFrac8S128VBar displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
        this.displayInstance = displayInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        vgraph = new PVGraphComponent(displayInstance.getN(), 128, -64, 64, axoObjectInstanceView);
        addChild(vgraph);
    }

    @Override
    public void updateV() {
        vgraph.setValue(displayInstance.getIDst());
    }
}
