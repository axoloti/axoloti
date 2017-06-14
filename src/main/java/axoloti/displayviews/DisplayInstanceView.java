package axoloti.displayviews;

import axoloti.atom.AtomDefinitionController;
import axoloti.displays.DisplayInstance;
import axoloti.displays.DisplayInstanceController;
import components.LabelComponent;
import java.beans.PropertyChangeEvent;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public abstract class DisplayInstanceView extends JPanel implements IDisplayInstanceView {

    final DisplayInstance displayInstance;
    DisplayInstanceController controller;
    LabelComponent label;
    
    DisplayInstanceView(DisplayInstance displayInstance, DisplayInstanceController controller) {
        this.displayInstance = displayInstance;
        this.controller = controller;
    }

    public void PostConstructor() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        if ((displayInstance.getDefinition().noLabel == null) || (displayInstance.getDefinition().noLabel == false)) {
            label = new LabelComponent(displayInstance.getDefinition().getName());
        } else {
            label = new LabelComponent("");
        }
        add(label);
        setSize(getPreferredSize());
	String description = displayInstance.getDefinition().getDescription();
	if (description != null) {
            setToolTipText(description);
        }
    }

    @Override
    public abstract void updateV();


    @Override
    public DisplayInstanceController getController() {
        return controller;
    }
    
    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(AtomDefinitionController.ATOM_NAME)) {
            label.setText((String)evt.getNewValue());
            doLayout();
        } else if (evt.getPropertyName().equals(AtomDefinitionController.ATOM_DESCRIPTION)) {
            setToolTipText((String)evt.getNewValue());
        }
    }
}
