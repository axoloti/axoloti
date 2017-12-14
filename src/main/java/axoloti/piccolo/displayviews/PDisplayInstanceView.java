package axoloti.piccolo.displayviews;

import axoloti.displays.DisplayInstance;
import axoloti.displayviews.IDisplayInstanceView;
import axoloti.mvc.AbstractController;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.piccolo.PatchPNode;
import components.piccolo.PLabelComponent;
import java.beans.PropertyChangeEvent;
import javax.swing.BoxLayout;

public abstract class PDisplayInstanceView extends PatchPNode implements IDisplayInstanceView {

    DisplayInstance displayInstance;
    IAxoObjectInstanceView axoObjectInstanceView;

    PDisplayInstanceView(DisplayInstance displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView.getPatchView());
        this.displayInstance = displayInstance;
        this.axoObjectInstanceView = axoObjectInstanceView;
    }

    public void PostConstructor() {
        setLayout(new BoxLayout(getProxyComponent(), BoxLayout.LINE_AXIS));
        setPickable(false);
        if ((displayInstance.getModel().noLabel == null) || (displayInstance.getModel().noLabel == false)) {
            addChild(new PLabelComponent(displayInstance.getModel().getName()));
        }
        setSize(getPreferredSize());
    }

    public abstract void updateV();

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        updateV();
    }

    @Override
    public AbstractController getController() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void dispose() {
    }
}
