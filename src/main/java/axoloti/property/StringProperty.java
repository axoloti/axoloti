package axoloti.property;

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
    public String getAsString(Object o) {
        String s = get(o);
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }

    @Override
    public String StringToObj(String v) {
        if (!allowNull() && (v == null)) {
            throw new Error("null String");
        }
        return v;
    }

}
