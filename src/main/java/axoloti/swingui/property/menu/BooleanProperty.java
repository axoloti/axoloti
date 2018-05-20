package axoloti.swingui.property.menu;

import axoloti.mvc.IModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;

/**
 *
 * @author jtaelman
 */
public class BooleanProperty extends JCheckBoxMenuItem {

    public BooleanProperty(IModel model, axoloti.property.BooleanProperty property) {
        super(property.getFriendlyName());
        axoloti.property.BooleanProperty p = (axoloti.property.BooleanProperty) property;
        Boolean v = p.get(model);
        if (v == null) {
            v = false;
        }
        super.setSelected(v);
        super.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (isSelected()) {
                    model.getController().addMetaUndo("set " + property.getFriendlyName());
                } else {
                    model.getController().addMetaUndo("clear " + property.getFriendlyName());
                }
                model.getController().generic_setModelUndoableProperty(property, isSelected());
            }
        });
    }

}
