package axoloti.property;

import axoloti.mvc.IModel;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jtaelman
 */
public abstract class PropertyReadWrite<T extends Object> extends Property<T> {

    final Method getter;
    final Method setter;

    public PropertyReadWrite(String name, Class propertyClass, Class containerClass, String friendlyName) {
        super(name, propertyClass, containerClass, friendlyName);
        Method s = null;
        Method g = null;
        try {
            g = containerClass.getMethod("get" + name, new Class[]{});
            s = containerClass.getMethod("set" + name, new Class[]{propertyClass});
        } catch (NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(Property.class.getName()).log(Level.SEVERE, containerClass.getName() + ":" + name, ex);
        }
        getter = g;
        setter = s;
    }

    @Override
    public T get(IModel o) {
        if (o == null) {
            throw new Error("getter from null model?");
        }
        try {
            Object r = getter.invoke(o);
            return (T) r;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Property.class.getName()).log(Level.SEVERE, o.getClass().getName() + ".get" + getName() + "()", ex);
        }
        return null;
    }

    @Override
    public void set(IModel obj, Object val) {
        try {
            //System.out.println("set property " + getFriendlyName() + " new: " + val.toString());
            setter.invoke(obj, val);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Property.class.getName()).log(Level.SEVERE, obj.getClass().getName() + ".set" + getName() + "(" + val.getClass().getName() + ")", ex);
        }
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

}
