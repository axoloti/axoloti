package axoloti.swingui.property.menu;

import axoloti.mvc.IModel;
import axoloti.property.MidiCCProperty;
import axoloti.property.Property;
import java.awt.Component;

/**
 *
 * @author jtaelman
 */
public class ViewFactory {

    private ViewFactory() {
    }

    /*
    public static Component createView(Property property) {
        if (property instanceof BooleanProperty) {
            return null;
        } else {
            return null;
        }
    }
     */
    public static Component createMenuItemView(IModel model, Property property) {
        if (property instanceof axoloti.property.BooleanProperty) {
            return new BooleanPropertyView(model, (axoloti.property.BooleanProperty) property);
        } else if (property instanceof MidiCCProperty) {
            return new AssignMidiCCMenuItems(model, (MidiCCProperty) property);
        } else {
            return null;
        }
    }

}
