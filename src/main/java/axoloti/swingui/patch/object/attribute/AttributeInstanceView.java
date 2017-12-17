package axoloti.swingui.patch.object.attribute;

import axoloti.preferences.Theme;
import axoloti.abstractui.IAttributeInstanceView;
import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.attribute.AttributeInstanceController;
import axoloti.swingui.mvc.ViewPanel;
import axoloti.swingui.components.LabelComponent;
import java.beans.PropertyChangeEvent;
import javax.swing.BoxLayout;

public abstract class AttributeInstanceView extends ViewPanel<AttributeInstanceController> implements IAttributeInstanceView {

    IAxoObjectInstanceView axoObjectInstanceView;

    LabelComponent label;

    AttributeInstanceView(AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller);
        this.axoObjectInstanceView = axoObjectInstanceView;
    }

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
    public void dispose() {
    }

}
