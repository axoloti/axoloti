package axoloti.swingui.patch.object.display;

import axoloti.abstractui.IDisplayInstanceView;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.display.DisplayInstanceController;
import axoloti.swingui.mvc.ViewPanel;
import axoloti.swingui.components.LabelComponent;
import java.beans.PropertyChangeEvent;
import javax.swing.BoxLayout;

abstract class DisplayInstanceView extends ViewPanel<DisplayInstanceController> implements IDisplayInstanceView {

    LabelComponent label;

    DisplayInstanceView(DisplayInstanceController controller) {
        super(controller);
    }

    DisplayInstance getModel() {
        return getController().getModel();
    }

    void PostConstructor() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        label = new LabelComponent("");
        add(label);
        setSize(getPreferredSize());
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (DisplayInstance.NAME.is(evt)) {
            label.setText((String) evt.getNewValue());
            doLayout();
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
