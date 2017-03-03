package components.piccolo.displays;

import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.piccolo.PatchPNode;

public abstract class PDispComponentAbstract extends PatchPNode {

    public PDispComponentAbstract(IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView.getPatchView());
    }

    abstract public void setValue(double value);

}
