package axoloti.attributeviews;

import axoloti.Theme;
import axoloti.atom.AtomDefinition;
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
        super();
        this.controller = controller;
        this.axoObjectInstanceView = axoObjectInstanceView;
    }

    public abstract void Lock();

    public abstract void UnLock();

    void PostConstructor() {
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
    public AttributeInstance getModel() {
        return getController().getModel();
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (AttributeInstance.NAME.is(evt)) {
            label.setText((String) evt.getNewValue());
            doLayout();
        } else if (AttributeInstance.DESCRIPTION.is(evt)) {
            setToolTipText((String) evt.getNewValue());
        }
    }

    @Override
    public AttributeInstanceController getController() {
        return controller;
    }

}
