package axoloti.displayviews;

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
        label = new LabelComponent("");
        add(label);
        setSize(getPreferredSize());
    }

    @Override
    public DisplayInstanceController getController() {
        return controller;
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
