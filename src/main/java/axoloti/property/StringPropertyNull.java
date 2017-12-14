package axoloti.property;

/**
 *
 * @author jtaelman
 */
public class StringPropertyNull extends PropertyReadWrite<String> {

    public StringPropertyNull(String name, Class containerClass) {
        super(name, String.class, containerClass, name);
    }

    public StringPropertyNull(String name, Class containerClass, String friendlyName) {
        super(name, String.class, containerClass, friendlyName);
    }

    @Override
    public Class getType() {
        return String.class;
    }

    @Override
    public boolean allowNull() {
        return true;
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
        return v;
    }
}
