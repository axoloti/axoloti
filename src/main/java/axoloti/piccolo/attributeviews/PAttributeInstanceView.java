package axoloti.piccolo.attributeviews;

import axoloti.PatchView;
import axoloti.attribute.AttributeInstance;
import axoloti.attribute.AttributeInstanceController;
import axoloti.attributeviews.IAttributeInstanceView;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.piccolo.PatchPNode;
import components.piccolo.PLabelComponent;
import java.beans.PropertyChangeEvent;
import javax.swing.BoxLayout;

public abstract class PAttributeInstanceView extends PatchPNode implements IAttributeInstanceView {

    IAxoObjectInstanceView axoObjectInstanceView;

    AttributeInstance attributeInstance;

    PAttributeInstanceView(AttributeInstance attributeInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView.getPatchView());
        this.attributeInstance = attributeInstance;
        this.axoObjectInstanceView = axoObjectInstanceView;
    }

    @Override
    public abstract void Lock();

    @Override
    public abstract void UnLock();

    void PostConstructor() {
        setLayout(new BoxLayout(getProxyComponent(), BoxLayout.LINE_AXIS));
        setPickable(false);
        addChild(new PLabelComponent(attributeInstance.getModel().getName()));
        setSize(getPreferredSize());
    }

    @Override
    public String getName() {
        if (attributeInstance != null) {
            return attributeInstance.getName();
        } else {
            return super.getName();
        }
    }

    public PatchView getPatchView() {
        return axoObjectInstanceView.getPatchView();
    }

    @Override
    public AttributeInstance getModel() {
        return attributeInstance;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AttributeInstanceController getController() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void dispose() {
    }

}
