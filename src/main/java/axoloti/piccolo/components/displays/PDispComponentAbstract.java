package axoloti.piccolo.components.displays;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.piccolo.patch.PatchPNode;

abstract class PDispComponentAbstract extends PatchPNode {

    public PDispComponentAbstract(IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView.getPatchView());
    }

    abstract public void setValue(double value);

}
