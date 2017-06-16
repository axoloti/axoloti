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

    final AttributeInstanceController controller;

    LabelComponent label;

    AttributeInstanceView(AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        this.controller = controller;
        this.axoObjectInstanceView = axoObjectInstanceView;
    }

    public abstract void Lock();

    public abstract void UnLock();

    public void PostConstructor() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setBackground(Theme.getCurrentTheme().Object_Default_Background);
        label = new LabelComponent(getModel().getModel().getName());
        add(label);
        setSize(getPreferredSize());
        String description = getModel().getModel().getDescription();
        if (description != null) {
            setToolTipText(description);
        }
    }

    @Override
    public String getName() {
        if (getModel() != null) {
            return getModel().getAttributeName();
        } else {
            return super.getName();
        }
    }

    @Override
    public PatchViewSwing getPatchView() {
        return (PatchViewSwing) axoObjectInstanceView.getPatchView();
    }

    @Override
    public AttributeInstance getModel() {
        return getController().getModel();
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
