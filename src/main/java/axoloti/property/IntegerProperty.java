package axoloti.property;

import axoloti.mvc.IModel;

/**
 *
 * @author jtaelman
 */
public class IntegerProperty extends PropertyReadWrite<Integer> {

    public IntegerProperty(String name, Class containerClass) {
        super(name, Integer.class, containerClass, name);
    }

    public IntegerProperty(String name, Class containerClass, String friendlyName) {
        super(name, Integer.class, containerClass, friendlyName);
    }

    @Override
    public Class getType() {
        return Integer.class;
    }

    @Override
    public boolean allowNull() {
        return false;
    }

    @Override
    public String getAsString(IModel o) {
        return Integer.toString(get(o));
    }

    @Override
    public Integer convertStringToObj(String v) {
        if (allowNull() && ((v == null) || v.isEmpty())) {
            return null;
        }
        return Integer.valueOf(v);
    }

}
