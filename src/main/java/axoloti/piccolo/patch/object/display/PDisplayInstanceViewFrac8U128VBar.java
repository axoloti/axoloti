package axoloti.piccolo.patch.object.display;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.display.DisplayInstanceFrac8U128VBar;
import axoloti.piccolo.components.PVGraphComponent;
import java.beans.PropertyChangeEvent;

class PDisplayInstanceViewFrac8U128VBar extends PDisplayInstanceView {

    private PVGraphComponent vgraph;
    private final IAxoObjectInstanceView axoObjectInstanceView;

    PDisplayInstanceViewFrac8U128VBar(DisplayInstance displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
	this.axoObjectInstanceView = axoObjectInstanceView;
        initComponents();
    }

    @Override
    public DisplayInstanceFrac8U128VBar getDModel() {
        return (DisplayInstanceFrac8U128VBar) super.getDModel();
    }

    private void initComponents() {
        vgraph = new PVGraphComponent(getDModel().getN(), 128, 0, 128, axoObjectInstanceView);
        addChild(vgraph);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (DisplayInstance.DISP_VALUE.is(evt)) {
            vgraph.setValue(getDModel().getIDst());
        }
    }
}
