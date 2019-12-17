package axoloti.property;

import axoloti.datatypes.ValueFrac32;
import axoloti.mvc.IModel;

/**
 *
 * @author jtaelman
 */
public class Frac32Property extends PropertyReadWrite<ValueFrac32> {

    public Frac32Property(String name, Class containerClass, String friendlyName) {
        super(name, ValueFrac32.class,containerClass,  friendlyName);
    }

    @Override
    public Class getType() {
        return ValueFrac32.class;
    }

    @Override
    public boolean allowNull() {
        return true;
    }

    @Override
    public String getAsString(IModel o) {
        ValueFrac32 v = get(o);
        if (v == null) {
            return "";
        } else {
            return v.toString();
        }
    }

    @Override
    public ValueFrac32 convertStringToObj(String v) {
        if (allowNull() && ((v == null) || v.isEmpty())) {
            return null;
        }
        return new ValueFrac32(Double.valueOf(v));
    }

}
