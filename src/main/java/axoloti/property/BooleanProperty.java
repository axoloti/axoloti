package axoloti.property;

import axoloti.mvc.IModel;

/**
 *
 * @author jtaelman
 */
public class BooleanProperty extends PropertyReadWrite<Boolean> {

    public BooleanProperty(String name, Class containerClass, String friendlyName) {
        super(name, Boolean.class, containerClass, friendlyName);
    }

    public BooleanProperty(String name, Class containerClass) {
        super(name,  Boolean.class,containerClass, name);
    }

    @Override
    public Class getType() {
        return Boolean.class;
    }

    @Override
    public boolean allowNull() {
        return true;
    }

    @Override
    public String getAsString(IModel o) {
        Boolean v = get(o);
        if (v == null) {
            return "";
        } else {
            return Boolean.toString(v);
        }
    }

    @Override
    public Boolean convertStringToObj(String v) {
        if (allowNull() && ((v == null) || v.isEmpty())) {
            return null;
        }
        return Boolean.valueOf(v);
    }
}
