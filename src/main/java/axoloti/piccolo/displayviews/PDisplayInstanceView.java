package axoloti.piccolo.displayviews;

import axoloti.ModelChangedListener;
import axoloti.displays.DisplayInstance;
import axoloti.displayviews.IDisplayInstanceView;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.piccolo.PatchPNode;
import components.piccolo.PLabelComponent;
import javax.swing.BoxLayout;

public abstract class PDisplayInstanceView extends PatchPNode implements ModelChangedListener, IDisplayInstanceView {

    DisplayInstance displayInstance;
    IAxoObjectInstanceView axoObjectInstanceView;

    PDisplayInstanceView(DisplayInstance displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView.getPatchView());
        this.displayInstance = displayInstance;
        this.axoObjectInstanceView = axoObjectInstanceView;
    }

    @Override
    public void PostConstructor() {
        setLayout(new BoxLayout(getProxyComponent(), BoxLayout.LINE_AXIS));
        setPickable(false);
        if ((displayInstance.getDefinition().noLabel == null) || (displayInstance.getDefinition().noLabel == false)) {
            addChild(new PLabelComponent(displayInstance.getDefinition().getName()));
        }
        setSize(getPreferredSize());
    }

    @Override
    public abstract void updateV();

    @Override
    public void modelChanged() {
        updateV();
    }
}
