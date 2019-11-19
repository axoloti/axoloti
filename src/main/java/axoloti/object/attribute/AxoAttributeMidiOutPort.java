package axoloti.object.attribute;

import axoloti.property.Property;
import axoloti.property.StringListProperty;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author jtaelman
 */
public class AxoAttributeMidiOutPort extends AxoAttribute {

    private static final String[] menuEntries = {
        "output port 1",
        "output port 2",
        "output port 3",
        "output port 4",
        "output port 5",
        "output port 6",
        "output port 7",
        "output port 8",
        "input port 1",
        "input port 2",
        "input port 3",
        "input port 4",
        "input port 5",
        "input port 6",
        "input port 7",
        "input port 8"};
    private static final String[] cEntries = {
        "0", "1", "2", "3", "4", "5", "6", "7",
        "8", "9", "10", "11", "12", "13", "14", "15"
    };

    private static final List<String> MenuEntries = Arrays.asList(menuEntries);
    private static final List<String> CEntries = Arrays.asList(cEntries);

    public static final Property ATOM_MENUENTRIES = new StringListProperty("MenuEntries", AxoAttributeMidiOutPort.class, "Menu entries");
    public static final Property ATOM_CENTRIES = new StringListProperty("CEntries", AxoAttributeMidiOutPort.class, "C++ entries");

    public AxoAttributeMidiOutPort() {
    }

    public List<String> getMenuEntries() {
        return Collections.unmodifiableList(MenuEntries);
    }

    public void setMenuEntries(List<String> MenuEntries) {
        throw new UnsupportedOperationException();
    }

    public List<String> getCEntries() {
        return Collections.unmodifiableList(CEntries);
    }

    public void setCEntries(List<String> CEntries) {
        throw new UnsupportedOperationException();
    }

    static public final String TYPE_NAME = "midiOutPort";

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public List<Property> getEditableFields() {
        List l = super.getEditableFields();
        return l;
    }

}
