package axoloti.property;

/**
 *
 * @author jtaelman
 */
public class ObjectROProperty extends PropertyReadOnly<Object> {

    final Class clazz;

    public ObjectROProperty(String name, Class propertyClass, Class containerClass) {
        super(name, propertyClass, containerClass, name);
        this.clazz = propertyClass;
    }

    @Override
    public Class getType() {
        return clazz;
    }

    @Override
    public boolean allowNull() {
        return true;
    }

    @Override
    public String getAsString(Object o) {
        Object v = get(o);
        if (v == null) {
            return "";
        } else {
            return v.toString();
        }
    }

    @Override
    public Object StringToObj(String v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
