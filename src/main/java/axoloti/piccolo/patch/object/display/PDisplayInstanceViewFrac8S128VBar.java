package axoloti.piccolo.patch.object.display;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.display.DisplayInstanceFrac8S128VBar;
import axoloti.piccolo.components.PVGraphComponent;
import java.beans.PropertyChangeEvent;

class PDisplayInstanceViewFrac8S128VBar extends PDisplayInstanceView {

    private PVGraphComponent vgraph;
    private final IAxoObjectInstanceView axoObjectInstanceView;

    PDisplayInstanceViewFrac8S128VBar(DisplayInstance displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
	this.axoObjectInstanceView = axoObjectInstanceView;
        initComponents();
    }

    private void initComponents() {
        vgraph = new PVGraphComponent(getDModel().getN(), 128, -64, 64, axoObjectInstanceView);
        addChild(vgraph);
    }

    @Override
    public DisplayInstanceFrac8S128VBar getDModel() {
	return (DisplayInstanceFrac8S128VBar) super.getDModel();
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (DisplayInstance.DISP_VALUE.is(evt)) {
            vgraph.setValue(getDModel().getIDst());
        }
    }
}
