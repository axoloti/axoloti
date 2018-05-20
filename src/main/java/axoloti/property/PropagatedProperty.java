package axoloti.property;

import axoloti.mvc.IModel;
import axoloti.mvc.IView;

/**
 *
 * @author jtaelman
 */
public class PropagatedProperty<T extends Object> extends Property<T> {

    final Class clazz;
    final Property parentProperty;

    public PropagatedProperty(Property parentProperty, Class containerClass) {
        super(parentProperty.getName(), parentProperty.getClass(), containerClass, parentProperty.getFriendlyName());
        this.parentProperty = parentProperty;
        clazz = parentProperty.getClass();
    }

    @Override
    public T get(IModel obj) {
        return (T) parentProperty.get(
                ((IView) obj).getDModel()
        );
    }

    @Override
    public void set(IModel obj, Object val) {
        throw new Error("Can''t set PropagatedProperty obj.getClass().getName() + \"::\" + getName()");
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
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public String getAsString(IModel o) {
        Object v = get(o);
        if (v == null) {
            return "";
        } else {
            return v.toString();
        }
    }

    @Override
    public T convertStringToObj(String v) {
        return (T) parentProperty.convertStringToObj(v);
    }

}
