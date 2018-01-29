package axoloti.piccolo.patch.object.display;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.display.DisplayInstanceController;
import axoloti.patch.object.display.DisplayInstanceFrac8U128VBar;
import axoloti.piccolo.components.PVGraphComponent;
import java.beans.PropertyChangeEvent;

public class PDisplayInstanceViewFrac8U128VBar extends PDisplayInstanceView {

    private PVGraphComponent vgraph;
    private IAxoObjectInstanceView axoObjectInstanceView;

    public PDisplayInstanceViewFrac8U128VBar(DisplayInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
	this.axoObjectInstanceView = axoObjectInstanceView;
    }

    @Override
    DisplayInstanceFrac8U128VBar getModel() {
        return (DisplayInstanceFrac8U128VBar) super.getModel();
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        vgraph = new PVGraphComponent(getModel().getN(), 128, 0, 128, axoObjectInstanceView);
        addChild(vgraph);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (DisplayInstance.DISP_VALUE.is(evt)) {
            vgraph.setValue(getModel().getIDst());
        }
    }
}
