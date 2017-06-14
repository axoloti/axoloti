package axoloti.attributeviews;

import axoloti.PatchViewSwing;
import axoloti.Theme;
import axoloti.atom.AtomDefinitionController;
import axoloti.atom.AtomInstanceView;
import axoloti.attribute.AttributeInstance;
import axoloti.attribute.AttributeInstanceController;
import axoloti.objectviews.IAxoObjectInstanceView;
import components.LabelComponent;
import java.beans.PropertyChangeEvent;
import javax.swing.BoxLayout;

public abstract class AttributeInstanceView extends AtomInstanceView implements IAttributeInstanceView {

    IAxoObjectInstanceView axoObjectInstanceView;

    AttributeInstance attributeInstance;

    final AttributeInstanceController controller;

    LabelComponent label;

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
        label = new LabelComponent(attributeInstance.getDefinition().getName());
        add(label);
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
        if (evt.getPropertyName().equals(AtomDefinitionController.ATOM_NAME)) {
            label.setText((String) evt.getNewValue());
            doLayout();
        } else if (evt.getPropertyName().equals(AtomDefinitionController.ATOM_DESCRIPTION)) {
            setToolTipText((String) evt.getNewValue());
        }
    }

    @Override
    public AttributeInstanceController getController() {
        return controller;
    }

}
