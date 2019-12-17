package axoloti.piccolo.patch.object.display;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.abstractui.IDisplayInstanceView;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.piccolo.components.PLabelComponent;
import axoloti.piccolo.patch.PatchPNode;
import java.beans.PropertyChangeEvent;
import javax.swing.BoxLayout;

abstract class PDisplayInstanceView extends PatchPNode implements IDisplayInstanceView {
    final protected DisplayInstance displayInstance;
    PLabelComponent label;

    PDisplayInstanceView(DisplayInstance displayInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView.getPatchView());
        this.displayInstance = displayInstance;
        initComponents();
    }

    @Override
    public DisplayInstance getDModel() {
        return displayInstance;
    }

    private void initComponents() {
        setLayout(new BoxLayout(getProxyComponent(), BoxLayout.LINE_AXIS));
        setPickable(false);
        label = new PLabelComponent("");
        addChild(label);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (DisplayInstance.NAME.is(evt)) {
            label.setText((String) evt.getNewValue());
            getProxyComponent().doLayout();
        } else if (DisplayInstance.NOLABEL.is(evt)) {
            Boolean b = (Boolean) evt.getNewValue();
            if (b == null) {
                b = false;
            }
            label.setVisible(!b);
        } else if (DisplayInstance.DESCRIPTION.is(evt)) {
            setToolTipText((String) evt.getNewValue());
        }
    }

    @Override
    public void dispose() {
    }
}
