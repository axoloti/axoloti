package axoloti.property;

import axoloti.mvc.IModel;

/**
 *
 * @author jtaelman
 */
public class DoubleProperty extends PropertyReadWrite<Double> {

    public DoubleProperty(String name, Class containerClass, String friendlyName) {
        super(name, Double.class,containerClass,  friendlyName);
    }

    @Override
    public Class getType() {
        return Double.class;
    }

    @Override
    public boolean allowNull() {
        return false;
    }

    @Override
    public String getAsString(IModel o) {
        return Double.toString(get(o));
    }

    @Override
    public Double convertStringToObj(String v) {
        if (allowNull() && ((v == null) || v.isEmpty())) {
            return null;
        }
        return Double.valueOf(v);
    }

}
