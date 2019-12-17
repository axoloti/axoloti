package axoloti.swingui.property.menu;

import axoloti.mvc.IModel;
import axoloti.property.BooleanProperty;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;

/**
 *
 * @author jtaelman
 */
public class BooleanPropertyView extends JCheckBoxMenuItem {

    public BooleanPropertyView(IModel model, BooleanProperty property) {
        super(property.getFriendlyName());
        BooleanProperty p = property;
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
