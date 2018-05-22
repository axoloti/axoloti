package axoloti.swingui.patch.object.attribute;

import axoloti.abstractui.IAttributeInstanceView;
import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.abstractui.PatchView;
import axoloti.mvc.FocusEdit;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.preferences.Theme;
import axoloti.swingui.components.LabelComponent;
import axoloti.swingui.mvc.ViewPanel;
import java.beans.PropertyChangeEvent;
import javax.swing.BoxLayout;

public abstract class AttributeInstanceView extends ViewPanel<AttributeInstance> implements IAttributeInstanceView {

    IAxoObjectInstanceView axoObjectInstanceView;

    LabelComponent label;

    AttributeInstanceView(AttributeInstance attribute, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attribute);
        this.axoObjectInstanceView = axoObjectInstanceView;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setBackground(Theme.getCurrentTheme().Object_Default_Background);
        label = new LabelComponent("");
        add(label);
    }

    private void scrollTo() {
        if (axoObjectInstanceView == null) {
            return;
        }
        PatchView pv = axoObjectInstanceView.getPatchView();
        if (pv == null) {
            return;
        }
        pv.scrollTo(this);
    }

    FocusEdit focusEdit = new FocusEdit() {

        @Override
        protected void focus() {
            scrollTo();
        }

    };

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (AttributeInstance.NAME.is(evt)) {
            label.setText((String) evt.getNewValue());
            doLayout();
        } else if (AttributeInstance.DESCRIPTION.is(evt)) {
            String s = (String) evt.getNewValue();
            if ((s != null) && (s.isEmpty())) {
                s = null;
            }
            setToolTipText(s);
        }
    }

    @Override
    public void dispose() {
    }

}
