package axoloti.attributeviews;

import axoloti.PatchViewSwing;
import axoloti.Theme;
import axoloti.atom.AtomInstanceView;
import axoloti.attribute.AttributeInstance;
import axoloti.attribute.AttributeInstanceComboBox;
import axoloti.attribute.AttributeInstanceController;
import axoloti.attribute.AttributeInstanceInt32;
import axoloti.attribute.AttributeInstanceObjRef;
import axoloti.attribute.AttributeInstanceSDFile;
import axoloti.attribute.AttributeInstanceSpinner;
import axoloti.attribute.AttributeInstanceTablename;
import axoloti.attribute.AttributeInstanceTextEditor;
import axoloti.mvc.AbstractController;
import axoloti.objectviews.AxoObjectInstanceView;
import axoloti.objectviews.IAxoObjectInstanceView;
import components.LabelComponent;
import java.beans.PropertyChangeEvent;
import javax.swing.BoxLayout;

public abstract class AttributeInstanceView extends AtomInstanceView implements IAttributeInstanceView {

    IAxoObjectInstanceView axoObjectInstanceView;
    PatchViewSwing patchView;

    AttributeInstance attributeInstance;

    final AttributeInstanceController controller;

    AttributeInstanceView(AttributeInstance attributeInstance, AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        this.attributeInstance = attributeInstance;
        this.controller = controller;
        this.axoObjectInstanceView = axoObjectInstanceView;
    }

    public abstract void Lock();

    public abstract void UnLock();

    public void PostConstructor() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setBackground(Theme.getCurrentTheme().Object_Default_Background);
        add(new LabelComponent(attributeInstance.getDefinition().getName()));
        setSize(getPreferredSize());
        String description = attributeInstance.getDefinition().getDescription();
        if (description != null) {
            setToolTipText(description);
        }
    }

    @Override
    public String getName() {
        if (attributeInstance != null) {
            return attributeInstance.getAttributeName();
        } else {
            return super.getName();
        }
    }

    @Override
    public PatchViewSwing getPatchView() {
        return (PatchViewSwing) axoObjectInstanceView.getPatchView();
    }

    public AttributeInstance getAttributeInstance() {
        return attributeInstance;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AbstractController getController() {
        return controller;
    }


}
