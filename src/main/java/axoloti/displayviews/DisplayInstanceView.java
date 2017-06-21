package axoloti.displayviews;

import axoloti.atom.AtomDefinitionController;
import axoloti.displays.DisplayInstance;
import axoloti.displays.DisplayInstanceController;
import components.LabelComponent;
import java.beans.PropertyChangeEvent;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

abstract class DisplayInstanceView extends JPanel implements IDisplayInstanceView {

    DisplayInstanceController controller;
    LabelComponent label;

    DisplayInstanceView(DisplayInstanceController controller) {
        this.controller = controller;
    }

    DisplayInstance getModel() {
        return getController().getModel();
    }

    void PostConstructor() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        if ((getModel().getModel().noLabel == null) || (getModel().getModel().noLabel == false)) {
            label = new LabelComponent(getModel().getModel().getName());
        } else {
            label = new LabelComponent("");
        }
        add(label);
        setSize(getPreferredSize());
    }

    @Override
    public DisplayInstanceController getController() {
        return controller;
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
}
