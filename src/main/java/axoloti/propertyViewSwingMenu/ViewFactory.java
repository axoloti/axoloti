package axoloti.propertyViewSwingMenu;

import axoloti.mvc.AbstractController;
import axoloti.property.MidiCCProperty;
import axoloti.property.Property;
import java.awt.Component;

/**
 *
 * @author jtaelman
 */
public class ViewFactory {

    /*
    public static Component createView(Property property) {
        if (property instanceof BooleanProperty) {
            return null;
        } else {
            return null;
        }
    }
     */
    public static Component createMenuItemView(AbstractController o, Property property) {
        if (property instanceof axoloti.property.BooleanProperty) {
            return new BooleanProperty(o, (axoloti.property.BooleanProperty) property);
        } else if (property instanceof MidiCCProperty) {
            return new AssignMidiCCMenuItems(o, (MidiCCProperty) property);
        } else {
            return null;
        }
    }

}
