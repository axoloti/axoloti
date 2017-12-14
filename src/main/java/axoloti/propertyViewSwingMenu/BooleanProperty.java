package axoloti.propertyViewSwingMenu;

import axoloti.mvc.AbstractController;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;

/**
 *
 * @author jtaelman
 */
public class BooleanProperty extends JCheckBoxMenuItem {

    public BooleanProperty(AbstractController o, axoloti.property.BooleanProperty property) {
        super(property.getFriendlyName());
        axoloti.property.BooleanProperty p = (axoloti.property.BooleanProperty) property;
        Boolean v = p.get(o.getModel());
        if (v == null) {
            v = false;
        }
        super.setSelected(v);
        super.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (isSelected()) {
                    o.addMetaUndo("set " + property.getFriendlyName());
                } else {
                    o.addMetaUndo("clear " + property.getFriendlyName());
                }
                o.setModelUndoableProperty(property, isSelected());
            }
        });
    }

}
