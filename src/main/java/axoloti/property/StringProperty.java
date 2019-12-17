package axoloti.property;

import axoloti.mvc.IModel;

/**
 *
 * @author jtaelman
 */
public class StringProperty extends PropertyReadWrite<String> {

    public StringProperty(String name, Class containerClass) {
        super(name, String.class, containerClass, name);
    }

    public StringProperty(String name, Class containerClass, String friendlyName) {
        super(name, String.class, containerClass, friendlyName);
    }

    @Override
    public Class getType() {
        return String.class;
    }

    @Override
    public boolean allowNull() {
        return false;
    }

    @Override
    public String getAsString(IModel o) {
        String s = get(o);
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }

    @Override
    public String convertStringToObj(String v) {
        if (!allowNull() && (v == null)) {
            throw new Error("null String");
        }
        return v;
    }

}
