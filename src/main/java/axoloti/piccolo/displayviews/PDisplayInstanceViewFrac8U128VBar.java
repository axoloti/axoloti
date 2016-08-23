package axoloti.piccolo.displayviews;

import axoloti.displays.DisplayInstanceFrac8U128VBar;
import axoloti.objectviews.IAxoObjectInstanceView;
import components.piccolo.PVGraphComponent;

public class PDisplayInstanceViewFrac8U128VBar extends PDisplayInstanceView {

    DisplayInstanceFrac8U128VBar displayInstance;
    private PVGraphComponent vgraph;

    public PDisplayInstanceViewFrac8U128VBar(DisplayInstanceFrac8U128VBar displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
        this.displayInstance = displayInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        vgraph = new PVGraphComponent(displayInstance.getN(), 128, 0, 128, axoObjectInstanceView);
        addChild(vgraph);
    }

    @Override
    public void updateV() {
        vgraph.setValue(displayInstance.getIDst());
    }
}
