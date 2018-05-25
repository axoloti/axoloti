package axoloti.piccolo.patch.object.display;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.piccolo.components.displays.PVValueLabelsComponent;
import java.beans.PropertyChangeEvent;

public class PDisplayInstanceViewVScale extends PDisplayInstanceView {
    private PVValueLabelsComponent vlabels;
    private final IAxoObjectInstanceView axoObjectInstanceView;


    public PDisplayInstanceViewVScale(DisplayInstance displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(displayInstance, axoObjectInstanceView);
	this.axoObjectInstanceView = axoObjectInstanceView;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
