package axoloti.piccolo.patch.object.attribute;

import axoloti.abstractui.IAttributeInstanceView;
import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.piccolo.components.PLabelComponent;
import axoloti.piccolo.patch.PatchPNode;
import java.beans.PropertyChangeEvent;
import javax.swing.BoxLayout;

public abstract class PAttributeInstanceView extends PatchPNode implements IAttributeInstanceView {

    IAxoObjectInstanceView axoObjectInstanceView;

    final AttributeInstance attribute;

    PLabelComponent label;

    PAttributeInstanceView(AttributeInstance attribute, IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView.getPatchView());
        this.attribute = attribute;
        this.axoObjectInstanceView = axoObjectInstanceView;
        initComponents();
    }

    @Override
    public abstract void lock();

    @Override
    public abstract void unlock();

    private void initComponents() {
        setLayout(new BoxLayout(getProxyComponent(), BoxLayout.LINE_AXIS));
        setPickable(false);
        label = new PLabelComponent(getDModel().getDModel().getName());
        addChild(label);
        String description = getDModel().getDModel().getDescription();
        if (description != null) {
            setToolTipText(description);
        }
    }

    @Override
    public AttributeInstance getDModel() {
        return attribute;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (AttributeInstance.NAME.is(evt)) {
            label.setText((String) evt.getNewValue());
            getProxyComponent().doLayout();
        } else if (AttributeInstance.DESCRIPTION.is(evt)) {
            setToolTipText((String) evt.getNewValue());
        }
    }

    @Override
    public void dispose() {
    }
}
