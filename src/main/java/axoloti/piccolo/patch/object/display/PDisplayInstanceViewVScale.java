package axoloti.piccolo.patch.object.display;

import java.beans.PropertyChangeEvent;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.display.DisplayInstanceController;
import axoloti.piccolo.components.displays.PVValueLabelsComponent;

public class PDisplayInstanceViewVScale extends PDisplayInstanceView {
    private PVValueLabelsComponent vlabels;
    private IAxoObjectInstanceView axoObjectInstanceView;


    public PDisplayInstanceViewVScale(DisplayInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
	this.axoObjectInstanceView = axoObjectInstanceView;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
